package com.simple.bank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Date;
import java.util.Calendar;

public class DateConstraintValidator implements ConstraintValidator<DateConstraint, Date>{
	
	@Override
    public void initialize(DateConstraint dateConstraint) {
    }

    @Override
    public boolean isValid(Date dob, ConstraintValidatorContext context) {
        Date today = new Date(new java.util.Date().getTime());
        Calendar obj = Calendar.getInstance();
        obj.setTime(today);
        obj.add(Calendar.YEAR, -100);
        return dob.compareTo(today) < 0 && dob.compareTo(obj.getTime()) > 0;
        		 
    }
 

}
