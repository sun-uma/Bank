package com.simple.bank.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TransactionTypeValidator implements ConstraintValidator<TransactionType, String>{
	
	@Override
    public void initialize(TransactionType transactionType) {
    }

    @Override
    public boolean isValid(String transactionType, ConstraintValidatorContext context) {
        return transactionType.compareToIgnoreCase("WITHDRAW") == 0 || 
        		transactionType.compareToIgnoreCase("DEPOSIT") == 0 ||
                transactionType.compareToIgnoreCase("TRANSFER") == 0;
    }
 
}
