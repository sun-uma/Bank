package com.simple.bank.repo;

import com.simple.bank.entity.LoanApplication;
import org.springframework.data.repository.CrudRepository;

public interface LoanApplicationRepository extends CrudRepository<LoanApplication, Integer> {
}
