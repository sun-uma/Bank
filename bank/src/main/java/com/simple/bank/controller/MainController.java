package com.simple.bank.controller;

import java.sql.Date;
import java.util.List;

import com.simple.bank.process.TransactionHandler;
import com.simple.bank.bo.AccountRequest;
import com.simple.bank.bo.LoanRequest;
import com.simple.bank.bo.TransactionRequest;
import com.simple.bank.entity.Account;
import com.simple.bank.entity.LoanApplication;
import com.simple.bank.entity.Transactions;
import com.simple.bank.repo.AccountRepository;
import com.simple.bank.repo.LoanApplicationRepository;
import com.simple.bank.repo.TransactionsRepository;
import com.simple.bank.utils.AccountUtils;
import com.simple.bank.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


@RestController
public class MainController {

	@Autowired private AccountRepository accountRepository;
	@Autowired private TransactionsRepository transactionsRepository;
	@Autowired private LoanApplicationRepository loanApplicationRepository;
	@Autowired private AccountUtils accountUtils;
	@Autowired private TransactionUtils transactionUtils;
	@Autowired private TransactionHandler transactionHandler;
	private static final Logger logger = LogManager.getLogger(MainController.class);
	
	@PostMapping(path="/new-account")
	public Account addNewCustomer (@Valid @RequestBody AccountRequest accountRequest) {
		logger.info("Calling new-account");
		return accountUtils.createAccount(accountRequest);
		
	}

	@PostMapping(path="/disable-account/{AccNo}")
	public Account disableAccount (@PathVariable long AccNo) {
		logger.info("Called disable-account for acc no: {}", AccNo);
		accountUtils.validateAccount(AccNo);
		return accountUtils.disableAccount(AccNo);

	}

	@PostMapping(path="/transaction/{AccNo}")
	public Transactions makeTransaction
	(@Valid @RequestBody TransactionRequest transactionRequest, @PathVariable long AccNo) {
		logger.info("Called transaction for acc no: {}", AccNo);
		accountUtils.validateAccount(AccNo);
		Transactions transaction = transactionUtils.createTransaction(transactionRequest, AccNo);
		return transactionHandler.execute(transaction);
	}

	@PostMapping(path="/transfer/")
	public Transactions makeTransfer
			(@RequestParam long depositorAcc, @RequestParam long receiverAcc,
			 @RequestBody TransactionRequest transactionRequest) {

		logger.info("Called transfer from acc no {} to {}", depositorAcc, receiverAcc);
		accountUtils.validateAccount(depositorAcc);
		accountUtils.validateAccount(receiverAcc);

		List<Transactions> transactionsList = transactionUtils.createTransactionForTransfer(transactionRequest, depositorAcc, receiverAcc);
		Transactions depositorTransaction = transactionHandler.execute(transactionsList.get(0));
		if(depositorTransaction.getStatus().compareTo("FAILURE") == 0) {
			logger.error("Transaction failed");
			return depositorTransaction;
		}
		logger.info("Amount withdrawn from {}", depositorAcc);

		transactionHandler.execute(transactionsList.get(1));
		logger.info("Amount deposited to acc no{}", receiverAcc);

		return depositorTransaction;
	}
	
	@GetMapping(path="/get-transactions/{AccNo}")
	public List<Transactions> getTransactions 
	(@RequestParam(defaultValue="accountCreated") String From, @RequestParam(defaultValue="today") String To,
			@PathVariable long AccNo) {
		logger.info("Called get-transactions for acc no: {}", AccNo);
		Account account = accountUtils.retrieveAccount(AccNo);
		
		Date from;
		if(From.compareTo("accountCreated") == 0)
			from = new Date(account.getCreated().getTime());
		else
			from = Date.valueOf(From);
		logger.info("From date: " + from.toString());
		
		Date to;
		if(To.compareTo("today") == 0)
			to = new Date(new java.util.Date().getTime());
		else
			to = Date.valueOf(To);
		logger.info("Last date: " + to.toString());
		
		return transactionsRepository.findByAccountBetweenDates(account.getAccNo(), from, to);
		
	}

	@PostMapping(path="/loan-apply/{AccNo}")
	public LoanApplication applyForLoan
			(@RequestBody LoanRequest loanRequest, @PathVariable long AccNo) {

		logger.info("Called loan-apply for acc no: {}", AccNo);
		accountUtils.validateAccount(AccNo);

		logger.info("Creating bank application");
		Account account = accountUtils.retrieveAccount(AccNo);
		LoanApplication loanApplication = new LoanApplication(account, loanRequest.getAmount());
		loanApplicationRepository.save(loanApplication);
		logger.info("Saved application to database");

		return loanApplication;
	}
}
