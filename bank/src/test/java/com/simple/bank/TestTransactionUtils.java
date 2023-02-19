package com.simple.bank;

import com.simple.bank.bo.TransactionRequest;
import com.simple.bank.entity.Account;
import com.simple.bank.entity.Transactions;
import com.simple.bank.utils.AccountUtils;
import com.simple.bank.utils.Constants;
import com.simple.bank.utils.TransactionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestTransactionUtils {

    @InjectMocks
    private TransactionUtils transactionUtils;

    @Mock
    private AccountUtils accountUtils;

    @Test
    public void testCreateTransaction() {
        Account account = new Account("Xavier", Date.valueOf("2010-12-12"),
                Constants.CURRENT, 5, 1000);
        when(accountUtils.retrieveAccount(anyLong())).thenReturn(account);
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(500);
        transactionRequest.setType(Constants.DEPOSIT);
        transactionRequest.setRemarks("Depositing Funds");

        Transactions transaction = transactionUtils.createTransaction(transactionRequest, 1L);
        assertEquals(1000, transaction.getOldBalance());
        assertEquals(1000, transaction.getNewBalance());
        assertEquals("Depositing Funds", transaction.getRemarks());
        assertEquals(Constants.TRANSACTIONS_STATUS_STARTED, transaction.getStatus());
    }

    @Test
    public void testCreateTransactionForTransfer() {
        Account depAccount = new Account("Xavier", Date.valueOf("2010-12-12"),
                Constants.CURRENT, 5, 1000);
        Account recAccount = new Account("Melody", Date.valueOf("2003-11-07"),
                Constants.SAVINGS, 0, 3000);
        when(accountUtils.retrieveAccount(1L)).thenReturn(depAccount);
        when(accountUtils.retrieveAccount(2L)).thenReturn(recAccount);
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(2000);
        transactionRequest.setType(Constants.TRANSFER);

        List<Transactions> transaction = transactionUtils.createTransactionForTransfer
                (transactionRequest, 1L, 2L);
        assertEquals(2, transaction.size());
        assertEquals(Constants.WITHDRAW, transaction.get(0).getType());
        assertEquals(Constants.DEPOSIT, transaction.get(1).getType());
        assertEquals("2/null", transaction.get(0).getRemarks());
        assertEquals("1/null", transaction.get(1).getRemarks());
    }
}
