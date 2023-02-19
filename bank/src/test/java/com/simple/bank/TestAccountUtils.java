package com.simple.bank;

import com.simple.bank.bo.AccountRequest;
import com.simple.bank.entity.Account;
import com.simple.bank.entity.Loan;
import com.simple.bank.repo.AccountRepository;
import com.simple.bank.repo.LoanRepository;
import com.simple.bank.utils.AccountUtils;
import com.simple.bank.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAccountUtils {

    long accountNumber;
    long disabledAccountNumber;
    long accountNumberToDisable;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Value("${current.account.transaction.fee}")
    private float currentAccTransactionFee;

    @Value("${savings.account.transaction.fee}")
    private float savingsAccTransactionFee;

    @Value("${loan.savings.interest.rate}")
    private float savingsLoanInterestRate;

    @Value("${loan.current.interest.rate}")
    private float currentLoanInterestRate;

    @Before
    public void init() {
        Account account = new Account("Xavier", Date.valueOf("2010-12-12"),
                Constants.CURRENT, currentAccTransactionFee, 1000);
        accountRepository.save(account);
        accountNumber = account.getAccNo();

        // Creating loan for above account for disabled checks to fail
        Loan newLoan = new Loan(account, 5000, Date.valueOf("2023-01-12"), 100);
        loanRepository.save(newLoan);

        Account accountToDisable = new Account("Elijah", Date.valueOf("2011-01-12"),
                Constants.SAVINGS, currentAccTransactionFee, 700);
        accountRepository.save(accountToDisable);
        accountNumberToDisable = accountToDisable.getAccNo();

        Account disabledAccount = new Account("Darling", Date.valueOf("2000-01-19"),
                Constants.SAVINGS, savingsAccTransactionFee, 500);
        disabledAccount.setDisabled(true);
        accountRepository.save(disabledAccount);
        disabledAccountNumber = disabledAccount.getAccNo();
    }

    @After
    public void destroy() {
        Loan loan = loanRepository.findByAccNoIsActive(accountNumber).get(0);
        loanRepository.delete(loan);
        accountRepository.deleteById(accountNumber);
        accountRepository.deleteById(disabledAccountNumber);
        accountRepository.deleteById(accountNumberToDisable);
    }

    @Test
    public void testRetrieveAccountSuccess() {
        Account expectedAccount = accountRepository.findById(accountNumber).get();
        Account actualAccount = accountUtils.retrieveAccount(accountNumber);
        assertEquals(expectedAccount.getName(), actualAccount.getName());
        assertEquals(expectedAccount.getBalance(), actualAccount.getBalance());
        assertEquals(expectedAccount.getAccType(), actualAccount.getAccType());
        assertEquals(expectedAccount.getDob(), actualAccount.getDob());
    }

    @Test
    public void testRetrieveAccountFail() {
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> accountUtils.retrieveAccount(-1L));
        assertTrue(exception.getMessage().contains("account not found"));
    }

    @Test
    public void testValidateAccountSuccess() {
        assertDoesNotThrow(() -> accountUtils.validateAccount(accountNumber));
    }

    @Test
    public void testValidateAccountFail() {
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> accountUtils.validateAccount(disabledAccountNumber));
        assertTrue(exception.getMessage().contains("Account disabled!"));
    }

    @Test
    public void testDisableAccountSuccess() {
        Account account = accountUtils.disableAccount(accountNumberToDisable);
        assertTrue(account.isDisabled());
        assertEquals(0, account.getBalance());
    }

    @Test
    public void testDisableAccountFail() {
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> accountUtils.disableAccount(accountNumber));
        assertTrue(exception.getMessage().contains("Account has outstanding loan!"));
    }

    @Test
    public void testGetTransactionFeeCurrent() {
        assertEquals(currentAccTransactionFee, accountUtils.getTransactionFee(Constants.CURRENT));
    }

    @Test
    public void testGetTransactionFeeSavings() {
        assertEquals(savingsAccTransactionFee, accountUtils.getTransactionFee(Constants.SAVINGS));
    }

    @Test
    public void testGetLoanInterestRateCurrent() {
        assertEquals(currentLoanInterestRate, accountUtils.getLoanInterestRate(Constants.CURRENT));
    }

    @Test
    public void testGetLoanInterestRateSavings() {
        assertEquals(savingsLoanInterestRate, accountUtils.getLoanInterestRate(Constants.SAVINGS));
    }

    @Test
    public void testCreateAccount() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setName("Joshua");
        accountRequest.setDob(Date.valueOf("2013-07-22"));
        accountRequest.setAccountType(Constants.CURRENT);
        accountRequest.setInitialDeposit(1000);

        Account createdAccount = accountUtils.createAccount(accountRequest);
        assertEquals(createdAccount.getTransactionFee(), currentAccTransactionFee);
        assertEquals(createdAccount.getBalance(), 1000);
        assertFalse(createdAccount.isDisabled());
        assertNull(createdAccount.getInterestLastCredited());

        accountRepository.delete(createdAccount);
    }

}
