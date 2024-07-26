package com.iroegbulam.princewill.mecash.service;

import com.iroegbulam.princewill.mecash.domain.Customer;
import com.iroegbulam.princewill.mecash.dto.CustomerRegistration;
import com.iroegbulam.princewill.mecash.exception.DuplicateException;
import com.iroegbulam.princewill.mecash.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(CustomerRegistration request) {
        String phoneNumber = request.phoneNumber();
        Optional<Customer> existingUser = repository.findByPhone(phoneNumber);
        if (existingUser.isPresent()) {
            throw new DuplicateException(String.format("User with the phone number '%s' already exists.", phoneNumber));
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        Customer user = new Customer(request.firstname(),request.middleName(),request.lastName()
                ,/*customer id genrator*/"",request.phoneNumber(),request.dateOfBirth(),
                request.email(),request.bvn(),request.nin(),hashedPassword);
        repository.add(user);
    }
}
