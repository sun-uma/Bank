package com.simple.bank.bo;

import javax.validation.constraints.NotNull;

public class LoanRequest {

    @NotNull
    private int creditScore;

    @NotNull
    private float amount;

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
}
