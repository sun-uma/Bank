package com.simple.bank;

import com.simple.bank.bo.AccountRequest;
import com.simple.bank.bo.LoanRequest;
import com.simple.bank.bo.TransactionRequest;
import com.simple.bank.controller.MainController;
import com.simple.bank.entity.Account;
import com.simple.bank.entity.Loan;
import com.simple.bank.entity.LoanApplication;
import com.simple.bank.entity.Transactions;
import com.simple.bank.process.LoanHandler;
import com.simple.bank.process.TransactionHandler;
import com.simple.bank.repo.LoanRepository;
import com.simple.bank.repo.TransactionsRepository;
import com.simple.bank.utils.AccountUtils;
import com.simple.bank.utils.Constants;
import com.simple.bank.utils.TransactionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMainController {

    @InjectMocks
    MainController mainController;

    @Mock
    AccountUtils accountUtils;

    @Mock
    TransactionUtils transactionUtils;

    @Mock
    TransactionHandler transactionHandler;

    @Mock
    LoanHandler loanHandler;

    @Mock
    TransactionsRepository transactionsRepository;

    @Mock
    LoanRepository loanRepository;

    @Value("${current.account.transaction.fee}")
    private float currentAccTransactionFee;

    @Value("${loan.current.interest.rate}")
    private float currentLoanAccInterestRate;

    @Test
    public void testMakeTransferFailInvalidTransactionType() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setType(Constants.WITHDRAW);
        try {
            mainController.makeTransfer(1L, 2L, transactionRequest);
            fail("Should throw Response status exception!");
        } catch(ResponseStatusException e) {
            assertThat(e.getMessage().contains("This API is only used for transfers!"));
        }
    }

    @Test
    public void testApplyForLoan() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setCreditScore(8);
        loanRequest.setAmount(1000);
        loanRequest.setTimeInYears(3);

        Account account = new Account();
        account.setName("Elizabeth");
        account.setDob(Date.valueOf("2022-12-12"));
        account.setAccType(Constants.CURRENT);
        LoanApplication loanApplication = new LoanApplication(account, currentLoanAccInterestRate,
                loanRequest);

        when(accountUtils.retrieveAccount(any(Long.class))).thenReturn(account);
        when(loanHandler.processLoan(any(LoanApplication.class))).thenReturn(loanApplication);

        LoanApplication actualLoanApplication = mainController.applyForLoan(loanRequest, account.getAccNo());

        assertEquals(loanApplication.getLoanAmount(), actualLoanApplication.getLoanAmount());
        assertEquals(loanApplication.getStatus(), actualLoanApplication.getStatus());
        assertEquals(loanApplication.getInterestRate(), actualLoanApplication.getInterestRate());
    }

    @Test
    public void testGetLoanDetailsEmpty() {
        when(loanRepository.findByAccNoIsActive(any(Long.class))).thenReturn(new ArrayList<Loan>());
        assertNull(mainController.getLoanDetails(1L));
    }

    @Test
    public void testGetLoanDetails() {
        List<Loan> loanList = new ArrayList<>();
        Loan loan1 = new Loan();
        loan1.setActive(true);
        loan1.setAccount(new Account());
        loan1.setTotalAmount(5000);
        loan1.setAmountPaid(2000);
        loan1.setAmountRemaining(3000);
        loan1.setRepaymentAmount(500);
        loan1.setLastPayment(Date.valueOf("2023-01-01"));
        loan1.setNextPayment(Date.valueOf("2023-02-01"));
        loanList.add(loan1);

        Loan loan2 = new Loan();
        loan2.setActive(false);
        loan2.setAccount(new Account());
        loan2.setTotalAmount(3000);
        loan2.setAmountRemaining(3000);
        loan2.setRepaymentAmount(100);
        loan2.setNextPayment(Date.valueOf("2023-02-01"));
        loanList.add(loan2);

        when(loanRepository.findByAccNoIsActive(any(Long.class))).thenReturn(loanList);
        Loan activeLoan = mainController.getLoanDetails(1L);
        assertTrue(activeLoan.isActive());
        assertFalse(activeLoan.isRedFlag());
        assertEquals(5000, activeLoan.getTotalAmount());
        assertEquals(2000, activeLoan.getAmountPaid());
        assertEquals(3000, activeLoan.getAmountRemaining());
        assertEquals(500, activeLoan.getRepaymentAmount());
        assertEquals(Date.valueOf("2023-01-01"), activeLoan.getLastPayment());
        assertEquals(Date.valueOf("2023-02-01"), activeLoan.getNextPayment());
    }
}
