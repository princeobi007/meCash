package com.iroegbulam.princewill.mecash.domain;

import com.iroegbulam.princewill.mecash.enums.TransactionCategory;
import com.iroegbulam.princewill.mecash.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table
@Entity(name = "transaction")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated
    private TransactionCategory transactionCategory;

    private double amount;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    private LocalDateTime createdAt;

    private String transactionRef;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
