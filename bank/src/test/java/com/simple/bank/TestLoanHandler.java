package com.simple.bank;

import com.simple.bank.entity.Account;
import com.simple.bank.entity.Loan;
import com.simple.bank.entity.LoanApplication;
import com.simple.bank.entity.Transactions;
import com.simple.bank.process.LoanHandler;
import com.simple.bank.repo.AccountRepository;
import com.simple.bank.repo.LoanApplicationRepository;
import com.simple.bank.repo.LoanRepository;
import com.simple.bank.repo.TransactionsRepository;
import com.simple.bank.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestLoanHandler {

    Account account;
    Loan loan;

    @Autowired
    private LoanHandler loanHandler;

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionsRepository transactionsRepository;

    @Value("${current.account.transaction.fee}")
    private float currentAccTransactionFee;

    @Value("${loan.current.interest.rate}")
    private float currentLoanInterestRate;

    @Before
    public void init() {
        account = new Account("Xavier", Date.valueOf("2010-12-12"),
                Constants.CURRENT, currentAccTransactionFee, 1000);
        accountRepository.save(account);

        loan = new Loan(account, 5000, Date.valueOf("2023-01-12"), 100);
        loanRepository.save(loan);
    }

    @After
    public void destroy() {
        loanRepository.delete(loan);
        accountRepository.delete(account);
    }

    @Test
    public void testIsEligibleForLoanFailInCreditScore() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setCreditScore(6);

        Method method = LoanHandler.class.getDeclaredMethod
                ("isEligibleForLoan", LoanApplication.class);
        method.setAccessible(true);
        Boolean result = (Boolean) method.invoke(loanHandler, loanApplication);
        assertFalse(result);
        assertEquals(Constants.CREDIT_SCORE_TOO_LOW, loanApplication.getRemarks());
    }

    @Test
    public void testIsEligibleForLoanFailInMinLoanAmount() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setCreditScore(7);
        loanApplication.setLoanAmount(99);

        Method method = LoanHandler.class.getDeclaredMethod
                ("isEligibleForLoan", LoanApplication.class);
        method.setAccessible(true);
        Boolean result = (Boolean) method.invoke(loanHandler, loanApplication);
        assertFalse(result);
        assertEquals(Constants.LOAN_AMOUNT_TOO_LOW, loanApplication.getRemarks());
    }

    @Test
    public void testIsEligibleForLoanFailInMaxLoanAmount() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setCreditScore(7);
        loanApplication.setLoanAmount(500001);

        Method method = LoanHandler.class.getDeclaredMethod
                ("isEligibleForLoan", LoanApplication.class);
        method.setAccessible(true);
        Boolean result = (Boolean) method.invoke(loanHandler, loanApplication);
        assertFalse(result);
        assertEquals(Constants.LOAN_AMOUNT_TOO_HIGH, loanApplication.getRemarks());
    }

    @Test
    public void testIsEligibleForLoanFailInExistingLoan() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setCreditScore(7);
        loanApplication.setLoanAmount(500000);
        loanApplication.setAccount(account);

        loan.setActive(true);
        loan.setRedFlag(false);
        loanRepository.save(loan);

        Method method = LoanHandler.class.getDeclaredMethod
                ("isEligibleForLoan", LoanApplication.class);
        method.setAccessible(true);
        Boolean result = (Boolean) method.invoke(loanHandler, loanApplication);
        assertFalse(result);
        assertEquals(Constants.HAS_EXISTING_LOAN_OR_RED_FLAGS, loanApplication.getRemarks());
    }

    @Test
    public void testIsEligibleForLoanFailInRedFlags() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setCreditScore(7);
        loanApplication.setLoanAmount(500000);
        loanApplication.setAccount(account);

        loan.setActive(false);
        loan.setRedFlag(true);
        loanRepository.save(loan);

        Method method = LoanHandler.class.getDeclaredMethod
                ("isEligibleForLoan", LoanApplication.class);
        method.setAccessible(true);
        Boolean result = (Boolean) method.invoke(loanHandler, loanApplication);
        assertFalse(result);
        assertEquals(Constants.HAS_EXISTING_LOAN_OR_RED_FLAGS, loanApplication.getRemarks());
    }

    @Test
    public void testIsEligibleForLoanPass() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setCreditScore(7);
        loanApplication.setLoanAmount(100);
        loanApplication.setAccount(new Account());

        Method method = LoanHandler.class.getDeclaredMethod
                ("isEligibleForLoan", LoanApplication.class);
        method.setAccessible(true);
        Boolean result = (Boolean) method.invoke(loanHandler, loanApplication);
        assertTrue(result);
    }

    @Test
    public void testProcessLoanRejected() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setCreditScore(7);
        loanApplication.setLoanAmount(500001);
        loanApplication.setAccount(account);

        loanHandler.processLoan(loanApplication);
        assertEquals(Constants.LOAN_APPLICATION_STATUS_REJECTED, loanApplication.getStatus());
        loanApplicationRepository.delete(loanApplication);
    }

    @Test
    public void testProcessLoanAccepted() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setCreditScore(7);
        loanApplication.setLoanAmount(100);
        loanApplication.setAccount(account);
        loanApplication.setInterestRate(currentLoanInterestRate);
        loanApplication.setTimeInYears(2);

        loan.setActive(false);
        loan.setRedFlag(false);
        loanRepository.save(loan);

        loanHandler.processLoan(loanApplication);
        assertEquals(Constants.LOAN_APPLICATION_STATUS_ACCEPTED, loanApplication.getStatus());
        loanApplicationRepository.delete(loanApplication);
        Loan createdLoan = loanRepository.findByAccNo(account.getAccNo()).get(1);
        loanRepository.delete(createdLoan);
        List<Transactions> transactionsList =
                transactionsRepository.findTransactionsFromAccNo(account.getAccNo());
        transactionsRepository.deleteAll(transactionsList);
    }

}
