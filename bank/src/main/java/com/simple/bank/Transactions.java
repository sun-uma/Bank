package com.simple.bank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
public class Transactions {
	
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	private int tid;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="account.accNo")
	private Account account;
	
	@NotNull
	private float amount;
	
	@NotNull
	private float oldBalance;
	
	@NotNull
	private float newBalance;
	
	@NotNull
	private String type;
	
	@NotNull
	private Date date;
	
	@NotNull
	private String status;
	
	@NotNull
	private Timestamp created;
	
	@NotNull
	private Timestamp updated;
	
	Transactions() {
		this.date = new Date (new java.util.Date().getTime());
		this.created = Timestamp.from(Instant.now());
		this.updated = created;
	}
	
	public void setAccount (Account account) {
		this.account = account;
		this.oldBalance = account.getBalance();
	}
	
	public Account getAccount () {
		return this.account;
	}
	
	public void setAmount (float amount) {
		this.amount = amount;
	}
	
	public float getAmount () {
		return this.amount;
	}
	
	public float getOldBalance () {
		return this.oldBalance;
	}
	
	public void setNewBalance (float newBalance) {
		this.newBalance = newBalance;
	}
	
	public float getNewBalance () {
		return this.newBalance;
	}
	
	public void setType (String type) {
		this.type = type;
	}
	
	public String getType () {
		return this.type;
	}
	
	public void setDate (Date date) {
		this.date = date;
	}
	
	public Date getDate () {
		return this.date;
	}
	
	public void setStatus (String status) {
		this.status = status;
	}
	
	public String getStatus () {
		return this.status;
	}
	
	public Timestamp getCreated () {
		return this.created;
	}
	
	public void setUpdated (Timestamp updated) {
		this.updated = updated;
	}
	
	public Timestamp getUpdated () {
		return this.updated;
	}
}
