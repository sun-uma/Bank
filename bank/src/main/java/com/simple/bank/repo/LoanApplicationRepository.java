package com.simple.bank.repo;

import com.simple.bank.entity.Loan;
import com.simple.bank.entity.LoanApplication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanApplicationRepository extends CrudRepository<LoanApplication, Integer> {

    @Query(value = "Select * from loan_application l where l.account_acc_no =:accNo and status = 'ACCEPTED'",
            nativeQuery=true)
    public List<LoanApplication> findAcceptedApplicationByAccNo(@Param("accNo") long AccNo);

}
