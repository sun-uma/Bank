package com.simple.bank;

import com.simple.bank.utils.Constants;
import com.simple.bank.validation.DateConstraintValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintValidatorContext;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class testDateConstraintValidator {

    @InjectMocks
    DateConstraintValidator dateConstraintValidator;

    @Mock
    ConstraintValidatorContext context;

    @Test
    public void testIsValidFailPast() {
        assertFalse(dateConstraintValidator.isValid(Date.valueOf("1923-01-01"), context));
    }

    @Test
    public void testIsValidFailFuture() {
        assertFalse(dateConstraintValidator.isValid(Date.valueOf("2050-01-01"), context));
    }

    @Test
    public void testIsValidCurrents() {
        assertTrue(dateConstraintValidator.isValid(Date.valueOf("2022-01-01"), context));
    }
}