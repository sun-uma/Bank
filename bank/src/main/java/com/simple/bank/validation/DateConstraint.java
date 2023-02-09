package com.simple.bank.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint (validatedBy = DateConstraintValidator.class)
@Retention(RUNTIME)
@Target ({ElementType.METHOD, ElementType.FIELD})
public @interface DateConstraint {
	
	String message() default "Invalid date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
