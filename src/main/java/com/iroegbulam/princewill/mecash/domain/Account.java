package com.iroegbulam.princewill.mecash.domain;

import com.iroegbulam.princewill.mecash.enums.AccountType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Table()
@Entity(name = "account")
@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(unique = true, nullable = false, length = 10)
    private String accountNumber;
    private String accountName;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @ManyToOne
    @JoinColumn(name = "account_currency_id")
    private Currency accountCurrency;
    private double availableBalance;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "account_signatory",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List <Customer>  signatories;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    private String registrationNumber;

    private LocalDateTime dateCreated;

    private LocalDateTime lastUpdated;

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setAccountCurrency(Currency accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
