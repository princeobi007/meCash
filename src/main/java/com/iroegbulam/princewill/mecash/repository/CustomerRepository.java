package com.iroegbulam.princewill.mecash.repository;

import com.iroegbulam.princewill.mecash.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByCustomerId(String customerId);

    Optional<Customer> findByUser_Login(String login);
}
