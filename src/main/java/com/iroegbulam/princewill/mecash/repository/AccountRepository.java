package com.iroegbulam.princewill.mecash.repository;

import com.iroegbulam.princewill.mecash.domain.Account;
import com.iroegbulam.princewill.mecash.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    List<Account> findByAccountNameIgnoreCaseAndAccountCurrency_CodeIgnoreCaseAndSignatories_CustomerIdInAndAccountType(String accountName, String code, Collection<String> customerIds, AccountType accountType);
}
