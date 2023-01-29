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

	@PostMapping(path="/disable-account/{AccNo}")
	public Account disableAccount (@PathVariable long AccNo) {

		logger.info("Searching for account");
		Optional<Account> account = accountRepository.findById(AccNo);
		if(!account.isPresent())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account not found");
		logger.info("Account found");

		Account realAccount = account.get();
		logger.info("Account is disabled: {}", account.get().isDisabled());
		if (account.get().isDisabled()) {
			throw new ResponseStatusException
					(HttpStatus.BAD_REQUEST, "Account already disabled!");
		}

		realAccount.setDisabled(true);
		realAccount.setBalance(0);
		accountRepository.save(realAccount);
		logger.info("Account disabled");

		return account.get();

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

		logger.info("Account is disabled: {}", account.get().isDisabled());
		if (account.get().isDisabled()) {
			throw new ResponseStatusException
					(HttpStatus.BAD_REQUEST, "Account is disabled!");
		}
		
		transaction.setAccount(account.get());
		transaction.setAmount(transactionRequest.getAmount());
		transaction.setType(transactionRequest.getType().toUpperCase());
		transaction.setRemarks(transactionRequest.getRemarks());

		logger.info("Old balance : {}", transaction.getOldBalance());
		
		logger.info("Starting transaction");
		MakeTransaction makeTransaction = new MakeTransaction(transaction);
		if(makeTransaction.makeTransaction())
			transaction.setStatus("SUCCESS");
		else
			transaction.setStatus("FAILURE");
		logger.info("Transaction completed " + transaction.getStatus());

		logger.info("New Balance: {}", transaction.getOldBalance());
		logger.info("Old Balance: {}", transaction.getNewBalance());
		
		//transaction.getAccount().addTransaction(transaction);
		
		transactionsRepository.save(transaction);
		logger.info("Saved transaction to database");
		return transaction;
	}

	@PostMapping(path="/transfer/")
	public Transactions makeTransfer
			(@RequestParam long depositorAcc, @RequestParam long receiverAcc,
			 @RequestBody TransactionRequest transactionRequest) {

		logger.info("Starting transfer");

		TransactionRequest depositorTransaction = new TransactionRequest();
		depositorTransaction.setAmount(transactionRequest.getAmount());
		depositorTransaction.setType("WITHDRAW");
		depositorTransaction.setRemarks(receiverAcc + "/" + transactionRequest.getRemarks());
		Transactions depositor = makeTransaction(depositorTransaction, depositorAcc);
		if(depositor.getStatus().compareTo("FAILURE") == 0) {
			logger.error("Transaction failed");
			return depositor;
		}

		logger.info("Amount withdrawn from {}", depositorAcc);

		TransactionRequest receiverTransaction = new TransactionRequest();
		receiverTransaction.setAmount(transactionRequest.getAmount());
		receiverTransaction.setType("DEPOSIT");
		receiverTransaction.setRemarks(depositorAcc + "/" + transactionRequest.getRemarks());

		try {
			makeTransaction(receiverTransaction, receiverAcc);
		} catch (Exception E) {
			logger.error("Could not deposit funds");

			logger.info("Rolling back transaction");
			TransactionRequest rollbackTransaction = new TransactionRequest();
			rollbackTransaction.setType("DEPOSIT");
			rollbackTransaction.setAmount(transactionRequest.getAmount());
			rollbackTransaction.setRemarks("Rollback Transaction/" + depositor.getTid() +
					"/" + receiverAcc);
			Transactions rollback = makeTransaction(rollbackTransaction, depositorAcc);

			logger.info("Transaction rolled back");
			return rollback;
		}

		logger.info("Amount deposited to acc no{}", receiverAcc);

		return depositor;
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
