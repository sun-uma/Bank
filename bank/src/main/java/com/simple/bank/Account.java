package com.simple.bank;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

import javax.validation.constraints.*;

@Entity
public class Account {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long accNo;
	
	@NotNull
	private String name;
	
	@NotNull
	private Date dob;
	
	@NotNull
	private String accType;
	
	@Min(0)
	private float transactionFee;
	
	@Min(0)
	@NotNull
	private float balance;
	
	@NotNull
	private Timestamp created;
	
	@NotNull
	private Timestamp updated;
	
	@OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Transactions> transactions;
	
	Account(String name, Date dob, String accType, float transactionFee, float balance) {
		this.name = name;
		this.dob = dob;
		this.accType = accType;
		this.transactionFee = transactionFee;
		this.balance = balance;
		created = Timestamp.from(Instant.now());
		updated = created;
	}
	
	Account() {
		created = Timestamp.from(Instant.now());
		updated = created;
	}
	
	public long getAccNo () {
		return this.accNo;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getName () {
		return this.name;
	}
	
	public void setDob (Date dob) {
		dob = this.dob;
	}
	
	public Date getDob () {
		return this.dob;
	}
	
	public void setAccType (String accType) {
		this.accType = accType;
	}
	
	public String getAccType () {
		return this.accType;
	}
	
	public void setTransactionFee (float transactionFee) {
		this.transactionFee = transactionFee;
	}
	
	public float getTransactionFee () {
		return this.transactionFee;
	}
	
	public void setBalance (float balance) {
		this.balance = balance;
	}
	
	public float getBalance () {
		return this.balance;
	}
	
	public Timestamp getCreated() {
		return this.created;
	}
	
	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}
	
	public Timestamp getUpdated() {
		return this.updated;
	}
	
	public void addTransaction (Transactions transaction) {
		transactions.add(transaction);
	}
	
	public Set<Transactions> getTransactions () {
		return this.transactions;
	}

}
