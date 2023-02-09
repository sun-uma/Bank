package com.simple.bank.bo;

import java.sql.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.simple.bank.validation.AccountType;
import com.simple.bank.validation.DateConstraint;
import org.springframework.format.annotation.DateTimeFormat;

//import javax.validation.constraints.NotBlank;

public class AccountRequest {
	
	@NotBlank(message = "Name cannot be blank")
	private String name;
	
	@DateTimeFormat (pattern="yyyy-MM-dd")
	@DateConstraint
	private Date dob;
	
	@AccountType
	private String accountType;
	
	@Min(0)
	private float initialDeposit;
	
	public AccountRequest() {
		this.initialDeposit = 0;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getName () {
		return this.name;
	}
	
	public void setDob (Date dob) {
		this.dob = dob;
	}
	
	public Date getDob () {
		return this.dob;
	}
	
	public void setAccountType (String accountType) {
		this.accountType = accountType;
	}
	
	public String getAccountType () {
		return this.accountType;
	}
	
	public void setInitialDeposit (float initialDeposit) {
		this.initialDeposit = initialDeposit;
	}
	
	public float getInitialDeposit() {
		return this.initialDeposit;
	}

}
