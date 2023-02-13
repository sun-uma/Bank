package com.simple.bank.controller;

import com.simple.bank.entity.Loan;
import com.simple.bank.entity.LoanApplication;
import com.simple.bank.repo.LoanApplicationRepository;
import com.simple.bank.repo.LoanRepository;
import com.simple.bank.utils.Constants;
import com.simple.bank.process.TransactionHandler;
import com.simple.bank.entity.Account;
import com.simple.bank.entity.Transactions;
import com.simple.bank.repo.AccountRepository;
import com.simple.bank.repo.TransactionsRepository;
import com.simple.bank.utils.InterestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.sql.Time;
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

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private TransactionHandler transactionHandler;

    @Autowired
    private InterestUtils interestUtils;

    @Value("${savings.account.interest.rate}")
    private float savingsAccInterestRate;

    @Value("${loan.savings.interest.rate}")
    private float savingsLoanInterestRate;

    @Value("${loan.current.interest.rate}")
    private float currentLoanInterestRate;

    @Value("${loan.defaulter.fine}")
    private float loanDefaulterFine;

    private static final Logger logger = LogManager.getLogger(CronController.class);

    @Scheduled(cron = "0 0 0 1 * ?")
    @PostMapping("/savings-acc-interest")
    public void addInterestForSavingsAccounts() {

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
                time = (float) diff.toDays()/accCreated.lengthOfMonth();

            } else {
                // Calculate interest from account last credited time
                LocalDate lastCredited = LocalDate.from(account.getInterestLastCredited().toLocalDateTime());
                LocalDate now = LocalDate.now();
                Duration diff = Duration.between(lastCredited.atStartOfDay(), now.atStartOfDay());
                logger.info("Days since last interest credited: " + diff.toDays());
                time = (float) diff.toDays()/lastCredited.lengthOfMonth();

            }

            float interest = interestUtils.calcInterest(account.getBalance(),time, savingsAccInterestRate);
            logger.info("Principal: {} Time: {} Rate: {} Interest: {}", account.getBalance(), time,
                    savingsAccInterestRate, interest);

            Transactions transaction = new Transactions(account, interest, Constants.DEPOSIT,
                    Constants.SAVING_ACCOUNT_INTEREST_REMARKS);
            transactionHandler.execute(transaction);
            logger.info("Saved transaction in database");
        }

        logger.info("Finished interest deposition for savings accounts!");
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    @PostMapping("/active-loan-emi")
    public void takeEMIOnActiveLoans() {
        logger.info("Started taking EMI for active loans!");
        List<Loan> activeLoans = loanRepository.findActiveLoans();
        for(Loan loan:activeLoans) {
            logger.info("Account picked by active loan emi cron: {}", loan.getAccount().getAccNo());
            Transactions transaction = new Transactions(loan.getAccount(), loan.getRepaymentAmount(),
                    Constants.WITHDRAW, Constants.LOAN_INTEREST_REMARKS);
            transactionHandler.execute(transaction);

            if(transaction.getStatus().compareTo(Constants.TRANSACTION_STATUS_FAILED) == 0) {
                logger.error("Customer unable to repay loan!!");
                loan.setRedFlag(true);
                loan.setAmountPaid(0);
                loan.setAmountRemaining(loan.getAmountRemaining() + loanDefaulterFine);
                loan.setRepaymentAmount(loan.getRepaymentAmount() + loanDefaulterFine);
            } else {
                logger.info("Customer repaid EMI amount {} on {}", loan.getRepaymentAmount(),
                        Date.from(Instant.now()));
                loan.setAmountPaid(loan.getAmountPaid() + loan.getRepaymentAmount());
                loan.setAmountRemaining(loan.getAmountRemaining() - loan.getRepaymentAmount());
                loan.setLastPayment(Date.from(Instant.now()));

                // customer defaulted earlier
                if(loan.isRedFlag()) {
                    logger.info("Resetting repayment amount");
                    List<LoanApplication> loanApplications = loanApplicationRepository.
                            findAcceptedApplicationByAccNo(loan.getAccount().getAccNo());
                    float repaymentAmount = interestUtils.findMonthlyRepaymentAmount(loanApplications.get(0).getLoanAmount(),
                            loanApplications.get(0).getInterestRate(), loanApplications.get(0).getTimeInYears());
                    logger.info("Repayment amount re-calculated: {}", (float)Math.ceil(repaymentAmount));
                    loan.setRepaymentAmount((float)Math.ceil(repaymentAmount));
                }

                // customer repaid complete loan
                if (loan.getAmountRemaining() == 0) {
                    logger.info("Customer completed loan process!");
                    loan.setActive(false);
                }
            }

            loan.setNextPayment(interestUtils.findFirstOfNextMonth());
            loan.setUpdatedDateTime(Timestamp.from(Instant.now()));
            loanRepository.save(loan);
        }
    }

}
