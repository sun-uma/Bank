package com.simple.bank;

import java.sql.Date;
import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


@RestController
public class MainController {

	@Autowired private AccountRepository accountRepository;
	@Autowired private TransactionsRepository transactionsRepository;
	private static final Logger logger = LogManager.getLogger(MainController.class);
	
	@PostMapping(path="/new-account")
	public Account addNewCustomer (@Valid @RequestBody AccountRequest accountRequest) {
		
		logger.info("Opening new account");
		float transactionFee = 0;
		if(accountRequest.getAccountType().compareToIgnoreCase("CURRENT") == 0)
			transactionFee = 5;
		Account account = new Account (accountRequest.getName(), 
				accountRequest.getDob(), accountRequest.getAccountType().toUpperCase(), 
				transactionFee, accountRequest.getInitialDeposit());
		logger.info("Created new account");
		
		accountRepository.save(account);
		logger.info("Saved account to database");
		
		return account;
		
	}

	@PostMapping(path="/transaction/{AccNo}")
	public Transactions makeTransaction 
	(@Valid @RequestBody TransactionRequest transactionRequest, @PathVariable long AccNo) {
		
		logger.info("Starting new transaction");
		Transactions transaction = new Transactions();
		
		logger.info("Searching for account");
		Optional<Account> account = accountRepository.findById(AccNo);
		if(!account.isPresent())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account not found");
		logger.info("Account found");
		
		transaction.setAccount(account.get());
		transaction.setAmount(transactionRequest.getAmount());
		transaction.setType(transactionRequest.getType().toUpperCase());
		
		logger.info("Starting transaction");
		MakeTransaction makeTransaction = new MakeTransaction(transaction);
		if(makeTransaction.makeTransaction())
			transaction.setStatus("SUCCESS");
		else
			transaction.setStatus("FAILURE");
		logger.info("Transaction completed " + transaction.getStatus());
		
		//transaction.getAccount().addTransaction(transaction);
		
		transactionsRepository.save(transaction);
		logger.info("Saved transaction to database");
		return transaction;
	}
	
	@GetMapping(path="/get-transactions/{AccNo}")
	public List<Transactions> getTransactions 
	(@RequestParam(defaultValue="accountCreated") String From, @RequestParam(defaultValue="today") String To,
			@PathVariable long AccNo) {
		
		logger.info("Searching for account");
		Optional<Account> account = accountRepository.findById(AccNo);
		if(!account.isPresent())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account not found");
		logger.info("Account found");
		
		Date from;
		if(From.compareTo("accountCreated") == 0)
			from = new Date(account.get().getCreated().getTime());
		else
			from = Date.valueOf(From);
		logger.info("From date: " + from.toString());
		
		Date to;
		if(To.compareTo("today") == 0)
			to = new Date(new java.util.Date().getTime());
		else
			to = Date.valueOf(To);
		logger.info("Last date: " + to.toString());
		
		return transactionsRepository.findByAccountBetweenDates(account.get().getAccNo(), from, to);
		
	}
	
}
