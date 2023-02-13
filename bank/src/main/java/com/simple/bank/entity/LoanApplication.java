package com.simple.bank.entity;

import com.simple.bank.bo.LoanRequest;
import com.simple.bank.utils.Constants;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
public class LoanApplication {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int loanApplicationId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account.accNo")
    private Account account;

    @NotNull
    @Min(0)
    private int creditScore;

    @NotNull
    private float loanAmount;

    @NotNull
    private float interestRate;

    @NotNull
    @Min(0)
    private float timeInYears;

    @NotNull
    private String status;

    private String remarks;

    @NotNull
    private final Timestamp createdDateTime;

    LoanApplication() {
        createdDateTime = Timestamp.from(Instant.now());
    }

    public LoanApplication(Account account, float interestRate, LoanRequest loanRequest) {
        this.account = account;
        this.timeInYears = loanRequest.getTimeInYears();
        this.loanAmount = loanRequest.getAmount();
        this.creditScore = loanRequest.getCreditScore();
        this.interestRate = interestRate;
        this.createdDateTime = Timestamp.from(Instant.now());
    }

    public int getLoanApplicationId() {
        return loanApplicationId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public float getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(float loanAmount) {
        this.loanAmount = loanAmount;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    public float getTimeInYears() {
        return timeInYears;
    }

    public void setTimeInYears(float timeInYears) {
        this.timeInYears = timeInYears;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Timestamp getCreatedDateTime() {
        return createdDateTime;
    }

}
