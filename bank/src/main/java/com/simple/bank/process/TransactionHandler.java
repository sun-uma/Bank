package com.simple.bank.process;

import com.simple.bank.entity.Transactions;
import com.simple.bank.repo.TransactionsRepository;
import com.simple.bank.utils.AccountUtils;
import com.simple.bank.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class TransactionHandler {

	@Autowired
	private TransactionsRepository transactionsRepository;
	@Autowired
	private AccountUtils accountUtils;

	private static final Logger logger = LogManager.getLogger(TransactionHandler.class);
	
	private void withdraw(Transactions transaction) {
		float transactionFee = accountUtils.getTransactionFee(transaction.getAccount().getAccType());
		if((transaction.getAmount() + transactionFee) > transaction.getOldBalance())
		{
			logger.error("Balance less than amount");
			throw new ResponseStatusException
			(HttpStatus.BAD_REQUEST, "Not enough funds to carry out transaction");
		}
			
		transaction.setNewBalance(transaction.getOldBalance() - (transaction.getAmount() + transactionFee));
		logger.info("Funds withdrawn successfully");
	}
	
	private void deposit (Transactions transaction) {
		transaction.setNewBalance(transaction.getOldBalance() + transaction.getAmount());
		logger.info("Funds deposited successfully");
	}

	public Transactions execute(Transactions transaction) {
		try {
			if (transaction.getType().compareTo("WITHDRAW") == 0)
				withdraw(transaction);
			else
				deposit(transaction);

			// updating account balance
			transaction.getAccount().setBalance(transaction.getNewBalance());
			transaction.getAccount().setUpdated(transaction.getUpdated());

			logger.info("Old balance: {}", transaction.getOldBalance());
			logger.info("New Balance: {}", transaction.getNewBalance());

			transaction.setStatus(Constants.TRANSACTIONS_STATUS_SUCCESS);
			transaction.setUpdated(Timestamp.from(Instant.now()));
			logger.info("Account updated, transaction complete");

		} catch (Exception e) {
			logger.error("Transaction failed");
			transaction.setStatus(Constants.TRANSACTION_STATUS_FAILED);
			transaction.setUpdated(Timestamp.from(Instant.now()));
		}

		transactionsRepository.save(transaction);
		return transaction;
	}
	
}
