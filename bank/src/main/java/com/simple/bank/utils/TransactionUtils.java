package com.simple.bank.utils;

import com.simple.bank.bo.TransactionRequest;
import com.simple.bank.entity.Transactions;
import com.simple.bank.repo.TransactionsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionUtils {

    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private AccountUtils accountUtils;

    private static final Logger logger = LogManager.getLogger(TransactionUtils.class);

    public Transactions createTransaction(TransactionRequest transactionRequest, long accNo) {
        logger.info("Starting new transaction");
        return new Transactions(accountUtils.retrieveAccount(accNo),
                transactionRequest.getAmount(), transactionRequest.getType().toUpperCase(),
                transactionRequest.getRemarks());
    }

    public List<Transactions> createTransactionForTransfer
            (TransactionRequest transactionRequest, long depositorAcc, long receiverAcc) {
        logger.info("Starting new transaction");
        Transactions t1 = new Transactions(accountUtils.retrieveAccount(depositorAcc),
                transactionRequest.getAmount(), Constants.WITHDRAW,
                receiverAcc + "/" + transactionRequest.getRemarks());
        Transactions t2 = new Transactions(accountUtils.retrieveAccount(receiverAcc),
                transactionRequest.getAmount(), Constants.DEPOSIT,
                depositorAcc + "/" + transactionRequest.getRemarks());

        List<Transactions> transactionsList = new ArrayList<Transactions>();
        transactionsList.add(t1);
        transactionsList.add(t2);

        return transactionsList;
    }

}
