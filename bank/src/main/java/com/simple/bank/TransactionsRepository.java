package com.simple.bank;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.sql.Date;

public interface TransactionsRepository extends CrudRepository<Transactions, Integer> {
	
	@Query(value = "Select * from Transactions t where t.account_acc_no =:accNo and t.date between :from and :to", 
			nativeQuery=true)
	public List<Transactions> findByAccountBetweenDates 
	(@Param("accNo") long AccNo, @Param("from") Date from, @Param("to") Date to);

}
