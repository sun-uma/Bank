package com.simple.bank.validation;

import com.simple.bank.validation.AccountType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountTypeValidator implements ConstraintValidator<AccountType, String>{
	
	@Override
    public void initialize(AccountType accountType) {
    }

    @Override
    public boolean isValid(String accountType, ConstraintValidatorContext context) {
        return accountType.compareToIgnoreCase("SAVINGS") == 0 || 
        		accountType.compareToIgnoreCase("CURRENT") == 0;
    }
 
}
