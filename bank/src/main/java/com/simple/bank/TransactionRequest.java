package com.simple.bank;

import javax.validation.constraints.*;

public class TransactionRequest {
	
	@Min(0)
	@NotNull
	private float amount;
	
	@NotNull
	@NotBlank (message="Type cannot be blank")
	@TransactionType
	private String type;

	private String remarks;
	
	TransactionRequest() { }
	
	public void setAmount (float amount) {
		this.amount = amount;
	}
	
	public float getAmount () {
		return this.amount;
	}
	
	public void setType (String type) {
		this.type = type;
	}
	
	public String getType () {
		return this.type;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


}
