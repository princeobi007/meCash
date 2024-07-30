package com.iroegbulam.princewill.mecash.repository;

import com.iroegbulam.princewill.mecash.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Page<Transaction> findByAccount_AccountNumber(String accountNumber, Pageable pageable);
    Page<Transaction> findByAccount_AccountNumberAndCreatedAtBetween(String accountNumber, LocalDateTime createdAtStart, LocalDateTime createdAtEnd, Pageable pageable);
}
