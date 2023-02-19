package com.simple.bank;

import com.simple.bank.entity.Account;
import com.simple.bank.entity.Transactions;
import com.simple.bank.process.TransactionHandler;
import com.simple.bank.repo.TransactionsRepository;
import com.simple.bank.utils.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestTransactionHandler {

    @InjectMocks
    TransactionHandler transactionHandler;

    @Mock
    TransactionsRepository transactionsRepository;

    @Value("${current.account.transaction.fee}")
    private float currentAccountTransactionFee;

    @Test
    public void testWithdrawPass() throws NoSuchMethodException {
        Transactions transaction = new Transactions();
        Account account = new Account();
        account.setTransactionFee(currentAccountTransactionFee);

        account.setBalance(1005);
        transaction.setAmount(1000);
        transaction.setAccount(account);

        Method method = TransactionHandler.class.getDeclaredMethod
                ("withdraw", Transactions.class);
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(transactionHandler, transaction));
        assertEquals(0, transaction.getNewBalance());
    }

    @Test
    public void testWithdrawFail() throws NoSuchMethodException {
        Transactions transaction = new Transactions();
        Account account = new Account();
        account.setTransactionFee(currentAccountTransactionFee);

        account.setBalance(1004);
        transaction.setAmount(1000);
        transaction.setAccount(account);

        Method method = TransactionHandler.class.getDeclaredMethod
                ("withdraw", Transactions.class);
        method.setAccessible(true);
        try {
            method.invoke(transactionHandler, transaction);
            fail("Should throw Response Status Exception for insufficient funds!");
        } catch (InvocationTargetException | IllegalAccessException exception) {
            assertThat(exception.getCause(), instanceOf(ResponseStatusException.class));
        }
    }

    @Test
    public void testDeposit() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Transactions transaction = new Transactions();
        Account account = new Account();
        account.setTransactionFee(currentAccountTransactionFee);

        account.setBalance(2000);
        transaction.setAmount(1000);
        transaction.setAccount(account);
        transaction.setDate(Date.valueOf("2022-12-12"));

        Method method = TransactionHandler.class.getDeclaredMethod
                ("deposit", Transactions.class);
        method.setAccessible(true);
        method.invoke(transactionHandler, transaction);
        assertEquals(3000, transaction.getNewBalance());
        assertEquals(Date.valueOf("2022-12-12"), transaction.getDate());
    }

    @Test
    public void testExecuteWithdrawFail() {
        Transactions transaction = new Transactions();
        Account account = new Account();
        account.setTransactionFee(currentAccountTransactionFee);

        account.setBalance(1004);
        transaction.setAmount(1000);
        transaction.setAccount(account);
        transaction.setType(Constants.WITHDRAW);
        transaction.setRemarks("Withdrawing Funds");

        when(transactionsRepository.save(any(Transactions.class))).thenReturn(transaction);
        transactionHandler.execute(transaction);
        assertEquals(Constants.TRANSACTION_STATUS_FAILED, transaction.getStatus());
        transactionsRepository.delete(transaction);
    }

    @Test
    public void testExecuteWithdrawPass() {
        Transactions transaction = new Transactions();
        Account account = new Account();
        account.setTransactionFee(currentAccountTransactionFee);

        account.setBalance(1005);
        transaction.setAmount(1000);
        transaction.setAccount(account);
        transaction.setType(Constants.WITHDRAW);

        when(transactionsRepository.save(any(Transactions.class))).thenReturn(transaction);
        transactionHandler.execute(transaction);
        assertEquals(Constants.TRANSACTIONS_STATUS_SUCCESS, transaction.getStatus());
        assertEquals(0, transaction.getNewBalance());
        transactionsRepository.delete(transaction);
    }

    @Test
    public void testExecuteDeposit() {
        Transactions transaction = new Transactions();
        Account account = new Account();
        account.setTransactionFee(currentAccountTransactionFee);

        account.setBalance(2000);
        transaction.setAmount(1000);
        transaction.setAccount(account);
        transaction.setType(Constants.DEPOSIT);

        when(transactionsRepository.save(any(Transactions.class))).thenReturn(transaction);
        transactionHandler.execute(transaction);
        assertEquals(Constants.TRANSACTIONS_STATUS_SUCCESS, transaction.getStatus());
        assertEquals(3000, transaction.getNewBalance());
        transactionsRepository.delete(transaction);
    }

}
