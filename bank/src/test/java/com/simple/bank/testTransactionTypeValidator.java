package com.simple.bank;

import com.simple.bank.utils.Constants;
import com.simple.bank.validation.AccountTypeValidator;
import com.simple.bank.validation.TransactionTypeValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class testTransactionTypeValidator {

    @InjectMocks
    TransactionTypeValidator transactionTypeValidator;

    @Mock
    ConstraintValidatorContext context;

    @Test
    public void testIsValidFail() {
        assertFalse(transactionTypeValidator.isValid("Some random string", context));
    }

    @Test
    public void testIsValidWithdraw() {
        assertTrue(transactionTypeValidator.isValid(Constants.WITHDRAW, context));
    }

    @Test
    public void testIsValidDeposit() {
        assertTrue(transactionTypeValidator.isValid(Constants.DEPOSIT, context));
    }

    @Test
    public void testIsValidTransfer() {
        assertTrue(transactionTypeValidator.isValid(Constants.TRANSFER, context));
    }
}
