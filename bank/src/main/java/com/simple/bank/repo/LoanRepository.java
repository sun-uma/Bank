package com.simple.bank.repo;

import com.simple.bank.entity.Loan;
import com.simple.bank.entity.LoanApplication;
import com.simple.bank.entity.Transactions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface LoanRepository extends CrudRepository<Loan, Integer> {

    @Query(value = "Select * from Loan l where l.account_acc_no =:accNo and " +
            "(is_active =1 or red_flag =1)", nativeQuery=true)
    public List<Loan> findByAccNoIsActiveOrRedFlags(@Param("accNo") long AccNo);

    @Query(value = "Select * from Loan l where l.account_acc_no =:accNo and is_active = 1", nativeQuery=true)
    public List<Loan> findByAccNoIsActive(@Param("accNo") long AccNo);

    @Query(value = "Select * from Loan where is_active = 1", nativeQuery=true)
    public List<Loan> findActiveLoans();
}
