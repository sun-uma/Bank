package com.simple.bank;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MakeTransaction {

	Transactions transaction;
	private static final Logger logger = LogManager.getLogger(MakeTransaction.class);
	
	MakeTransaction (Transactions transaction) {
		this.transaction = transaction;
	}
	
	private void withdraw() {
		if(transaction.getAmount() > transaction.getOldBalance())
		{
			logger.error("Balance less than amount");
			throw new ResponseStatusException
			(HttpStatus.BAD_REQUEST, "Not enough funds to carry out transaction");
		}
			
		transaction.setNewBalance(transaction.getOldBalance() - transaction.getAmount());
		logger.info("Funds withdrawn successfully");
	}
	
	private void deposit () {
		transaction.setNewBalance(transaction.getOldBalance() + transaction.getAmount());
		logger.info("Funds deposited successfully");
	}
	
	public boolean makeTransaction () {
		
		if(transaction.getType().compareTo("WITHDRAW") == 0)
			this.withdraw();
		else
			this.deposit();
		
		// transaction fee
		if(transaction.getAccount().getAccType().compareTo("CURRENT") == 0)
			transaction.setNewBalance(transaction.getNewBalance() - 5);
		
		// updating account balance
		transaction.getAccount().setBalance(transaction.getNewBalance());
		transaction.getAccount().setUpdated(transaction.getUpdated());
		
		logger.info("Account updated, transaction complete");
		
		return true;
	}
	
}
