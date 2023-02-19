package com.simple.bank;

import com.simple.bank.utils.Constants;
import com.simple.bank.validation.AccountTypeValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class testAccountTypeValidator {

    @InjectMocks
    AccountTypeValidator accountTypeValidator;

    @Mock
    ConstraintValidatorContext context;

    @Test
    public void testIsValidFail() {
        assertFalse(accountTypeValidator.isValid("Some random string", context));
    }

    @Test
    public void testIsValidSavings() {
        assertTrue(accountTypeValidator.isValid(Constants.SAVINGS, context));
    }

    @Test
    public void testIsValidCurrents() {
        assertTrue(accountTypeValidator.isValid(Constants.CURRENT, context));
    }
}
