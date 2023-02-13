package com.simple.bank.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Calendar;

@Service
public class InterestUtils {

    private static final Logger logger = LogManager.getLogger(AccountUtils.class);

    public float calcInterest(float principal, float rate, float timeInYears) {
        float interest = (principal * rate * timeInYears)/100;
        logger.info("Interest calculated: {}", interest);
        return interest;
    }

    public Date findFirstOfNextMonth () {
        Calendar cal = Calendar.getInstance();
        logger.info(cal);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        logger.info("First of next month: {}", cal);
        return cal.getTime();
    }

    public float findMonthlyRepaymentAmount(float principal, float rate, float timeInYears) {
        float interest = calcInterest(principal, rate, timeInYears);
        float monthlyRepaymentAmt = (principal + interest)/(timeInYears * 12);
        logger.info("Monthly repayment amount: {}", monthlyRepaymentAmt);
        return monthlyRepaymentAmt;
    }

}
