package com.simple.bank.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int loanId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account.accNo")
    private Account account;

    @NotNull
    @Min(0)
    private float totalAmount;

    @NotNull
    @Min(0)
    private float amountPaid;

    @NotNull
    @Min(0)
    private float amountRemaining;

    private Date lastPayment;

    @NotNull
    private Date nextPayment;

    @NotNull
    private float repaymentAmount;

    @NotNull
    private boolean isActive;

    @NotNull
    private boolean redFlag;

    @NotNull
    private final Timestamp createdDateTime;

    @NotNull
    private Timestamp updatedDateTime;

    Loan() {
        createdDateTime = Timestamp.from(Instant.now());
        updatedDateTime = Timestamp.from(Instant.now());
    }

    public Loan(Account account, float totalAmount, Date nextPayment, float repaymentAmount) {
        this.account = account;
        this.totalAmount = totalAmount;
        this.amountPaid = 0;
        this.amountRemaining = totalAmount;
        this.nextPayment = nextPayment;
        this.repaymentAmount = repaymentAmount;
        this.redFlag = false;
        this.isActive = true;
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

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(float amountPaid) {
        this.amountPaid = amountPaid;
    }

    public float getAmountRemaining() {
        return amountRemaining;
    }

    public void setAmountRemaining(float amountRemaining) {
        this.amountRemaining = amountRemaining;
    }

    public boolean isRedFlag() {
        return redFlag;
    }

    public void setRedFlag(boolean redFlag) {
        this.redFlag = redFlag;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
