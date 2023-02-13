package com.simple.bank.process;

import com.simple.bank.entity.Loan;
import com.simple.bank.entity.LoanApplication;
import com.simple.bank.entity.Transactions;
import com.simple.bank.repo.LoanApplicationRepository;
import com.simple.bank.repo.LoanRepository;
import com.simple.bank.utils.AccountUtils;
import com.simple.bank.utils.Constants;
import com.simple.bank.utils.InterestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanHandler {

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    TransactionHandler transactionHandler;

    @Autowired
    InterestUtils interestUtils;

    @Autowired
    AccountUtils accountUtils;

    @Value("${credit.score.cutoff}")
    private int creditScoreCutoff;

    @Value("${min.loan.amount}")
    private float minLoanAmount;

    @Value("${max.loan.amount}")
    private float maxLoanAmount;

    private static final Logger logger = LogManager.getLogger(AccountUtils.class);

    private boolean isEligibleForLoan(LoanApplication loanApplication) {

        if (loanApplication.getCreditScore() < creditScoreCutoff) {
            loanApplication.setRemarks(Constants.CREDIT_SCORE_TOO_LOW);
            logger.info("Credit score check failed");
            return false;
        }

        if (loanApplication.getLoanAmount() <= minLoanAmount) {
            loanApplication.setRemarks(Constants.LOAN_AMOUNT_TOO_LOW);
            logger.info("Loan amount too low");
            return false;
        }

        if (loanApplication.getLoanAmount() >= maxLoanAmount) {
            loanApplication.setRemarks(Constants.LOAN_AMOUNT_TOO_HIGH);
            logger.info("Loan amount too high");
            return false;
        }

        List<Loan> existingLoansOrRedFlag =
                loanRepository.findByAccNoIsActiveOrRedFlags(loanApplication.getAccount().getAccNo());
        if(!existingLoansOrRedFlag.isEmpty()) {
            loanApplication.setRemarks(Constants.HAS_EXISTING_LOAN_OR_RED_FLAGS);
            logger.info("Has existing loan or red flags (defaulters)");
            return false;
        }

        logger.info("Passed all checks");
        return true;
    }

    public LoanApplication processLoan(LoanApplication loanApplication) {
        if (!isEligibleForLoan(loanApplication)) {
            logger.info("Customer not eligible for loan!");
            loanApplication.setStatus(Constants.LOAN_APPLICATION_STATUS_REJECTED);
            loanApplicationRepository.save(loanApplication);
            return loanApplication;
        }

        logger.info("Loan successfully processed");
        Transactions transaction = new Transactions(loanApplication.getAccount(),
                loanApplication.getLoanAmount(), Constants.DEPOSIT, Constants.LOAN_PROCESSED_REMARKS);
        transactionHandler.execute(transaction);
        logger.info("Amount deposited into acc no: {}", loanApplication.getAccount().getAccNo());

        float repaymentAmount = interestUtils.findMonthlyRepaymentAmount(loanApplication.getLoanAmount(),
                loanApplication.getInterestRate(), loanApplication.getTimeInYears());

        float totalAmount = (float) Math.ceil(repaymentAmount) * loanApplication.getTimeInYears() * 12;

        Loan newLoan = new Loan(loanApplication.getAccount(), totalAmount,
                interestUtils.findFirstOfNextMonth(), (float) Math.ceil(repaymentAmount));
        logger.info("Created new loan object: {}", newLoan);
        loanRepository.save(newLoan);

        loanApplication.setStatus(Constants.LOAN_APPLICATION_STATUS_ACCEPTED);
        loanApplication.setRemarks(Constants.LOAN_PROCESSED_REMARKS);
        loanApplicationRepository.save(loanApplication);
        logger.info("Loan saved in repository");

        return loanApplication;
    }


}
