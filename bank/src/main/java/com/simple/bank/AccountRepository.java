package com.simple.bank;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {

    @Query(value = "Select * from Account where acc_type = 'SAVINGS' and disabled = 0", nativeQuery=true)
    public List<Account> findSavingsAccountsNotDisabled();

}
