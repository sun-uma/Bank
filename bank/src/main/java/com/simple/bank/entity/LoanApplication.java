package com.simple.bank.entity;

import com.simple.bank.utils.Constants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
public class LoanApplication {

    @Id
    @GeneratedValue
    private int loanId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "account.accNo")
    private Account account;

    @NotNull
    private String status;

    @NotNull
    private float loanAmount;

    private Date lastPayment;

    private Date nextPayment;

    private float repaymentAmount;

    @NotNull
    private final Timestamp createdDateTime;

    @NotNull
    private Timestamp updatedDateTime;

    public LoanApplication(Account account, float loanAmount) {
        this.account = account;
        this.status = Constants.LOAN_APPLICATION_STATUS_CREATED;
        this.loanAmount = loanAmount;
        this.createdDateTime = Timestamp.from(Instant.now());
        this.updatedDateTime = Timestamp.from(Instant.now());

    }

    public int getLoanId() {
        return loanId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(float loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Date getLastPayment() {
        return lastPayment;
    }

    public void setLastPayment(Date lastPayment) {
        this.lastPayment = lastPayment;
    }

    public Date getNextPayment() {
        return nextPayment;
    }

    public void setNextPayment(Date nextPayment) {
        this.nextPayment = nextPayment;
    }

    public float getRepaymentAmount() {
        return repaymentAmount;
    }

    public void setRepaymentAmount(float repaymentAmount) {
        this.repaymentAmount = repaymentAmount;
    }

    public Timestamp getCreatedDateTime() {
        return createdDateTime;
    }

    public Timestamp getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(Timestamp updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

}
