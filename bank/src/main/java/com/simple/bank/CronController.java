package com.simple.bank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RestController
@EnableScheduling
public class CronController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

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
                time = (float) diff.toDays()/accCreated.lengthOfYear();

            } else {
                // Calculate interest from account last credited time
                LocalDate lastCredited = LocalDate.from(account.getInterestLastCredited().toLocalDateTime());
                LocalDate now = LocalDate.now();
                Duration diff = Duration.between(lastCredited.atStartOfDay(), now.atStartOfDay());
                time = (float) diff.toDays()/lastCredited.lengthOfYear();

            }

            float interest = account.getBalance() * time * Constants.SAVINGS_ACCOUNT_INTEREST_RATE;
            logger.info("Principal: {} Time: {} Rate: {} Interest: {}", account.getBalance(), time,
                    Constants.SAVINGS_ACCOUNT_INTEREST_RATE, interest);

            Transactions transaction = new Transactions();
            transaction.setAccount(account);
            transaction.setAmount(interest);
            transaction.setType(Constants.DEPOSIT);
            transaction.setRemarks(Constants.INTEREST_REMARKS);

            MakeTransaction makeTransaction = new MakeTransaction(transaction);
            if(makeTransaction.makeTransaction())
                transaction.setStatus("SUCCESS");
            else
                transaction.setStatus("FAILURE");
            logger.info("Transaction completed " + transaction.getStatus());

            transaction.getAccount().setInterestLastCredited(Timestamp.from(Instant.now()));

            transactionsRepository.save(transaction);
            logger.info("Saved transaction in database");
        }

        logger.info("Finished interest deposition for savings accounts!");
    }

}
