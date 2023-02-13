package com.simple.bank.bo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class LoanRequest {

    @NotNull
    @Min(0)
    private int creditScore;

    @NotNull
    private float amount;

    @NotNull
    @Min(0)
    private float timeInYears;

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getTimeInYears() {
        return timeInYears;
    }

    public void setTimeInYears(float timeInYears) {
        this.timeInYears = timeInYears;
    }
}
