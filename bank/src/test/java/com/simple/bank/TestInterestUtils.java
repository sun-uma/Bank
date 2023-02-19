package com.simple.bank;

import com.simple.bank.utils.InterestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestInterestUtils {

    float principal;
    float rate;
    float timeInYears;

    @Autowired
    private InterestUtils interestUtils;

    @Before
    public void init() {
        principal = 3000;
        rate = 4;
        timeInYears = 2;
    }

    @Test
    public void testCalcInterest() {
        float interest = (principal * rate * timeInYears)/100;
        assertEquals(interest, interestUtils.calcInterest(principal, rate, timeInYears));
    }

    @Test
    public void testFindRepaymentAmount() {
        float interest = (principal * rate * timeInYears)/100;
        float repaymentAmount = (principal + interest)/(timeInYears * 12);
        assertEquals(repaymentAmount, interestUtils.findMonthlyRepaymentAmount
                (principal, rate, timeInYears));
    }

    @Test
    public void testFindFirstOfNextMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date expectedDate = cal.getTime();

        Date actualDate = interestUtils.findFirstOfNextMonth();
        assertEquals(expectedDate, actualDate);
    }
}
