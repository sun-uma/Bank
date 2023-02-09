package com.simple.bank.controller;

import com.simple.bank.utils.Constants;
import com.simple.bank.process.TransactionHandler;
import com.simple.bank.entity.Account;
import com.simple.bank.entity.Transactions;
import com.simple.bank.repo.AccountRepository;
import com.simple.bank.repo.TransactionsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@RestController
@EnableScheduling
public class CronController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private TransactionHandler transactionHandler;

    @Value("${savings.account.interest.rate}")
    private float savingsAccInterestRate;

    private static final Logger logger = LogManager.getLogger(CronController.class);

    @Scheduled(cron = "0 0 0 1 * ?")
    @PostMapping("/savings-acc-interest")
    public void addInterestForSavingsAccounts () {

        logger.info("Started interest deposition for savings accounts!");
        List<Account> accountList = accountRepository.findSavingsAccountsNotDisabled();
        float time;

        for(Account account:accountList) {
            logger.info("Account picked by cron: {}", account.getAccNo());
            Timestamp interestLastCredited = account.getInterestLastCredited();

            logger.info("Interest last credited: {}", interestLastCredited);
            if(interestLastCredited == null) {
                // Calculate interest from account created date till now
                LocalDate accCreated = LocalDate.from(account.getCreated().toLocalDateTime());
                LocalDate now = LocalDate.now();
                Duration diff = Duration.between(accCreated.atStartOfDay(), now.atStartOfDay());
                logger.info("Days since last interest credited: " + diff.toDays());
                time = (float) diff.toDays()/accCreated.lengthOfYear();

            } else {
                // Calculate interest from account last credited time
                LocalDate lastCredited = LocalDate.from(account.getInterestLastCredited().toLocalDateTime());
                LocalDate now = LocalDate.now();
                Duration diff = Duration.between(lastCredited.atStartOfDay(), now.atStartOfDay());
                logger.info("Days since last interest credited: " + diff.toDays());
                time = (float) diff.toDays()/lastCredited.lengthOfYear();

            }

            float interest = account.getBalance() * time * savingsAccInterestRate;
            logger.info("Principal: {} Time: {} Rate: {} Interest: {}", account.getBalance(), time,
                    savingsAccInterestRate, interest);

            Transactions transaction = new Transactions(account, interest, Constants.DEPOSIT,
                    Constants.SAVING_ACCOUNT_INTEREST_REMARKS);
            transactionHandler.execute(transaction);
            logger.info("Saved transaction in database");
        }

        logger.info("Finished interest deposition for savings accounts!");
    }

}
