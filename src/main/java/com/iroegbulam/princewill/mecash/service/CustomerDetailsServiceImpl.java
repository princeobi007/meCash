package com.iroegbulam.princewill.mecash.service;

import com.iroegbulam.princewill.mecash.domain.Customer;
import com.iroegbulam.princewill.mecash.exception.NotFoundException;
import com.iroegbulam.princewill.mecash.repository.CustomerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsServiceImpl implements UserDetailsService {
    private final CustomerRepository repository;

    public CustomerDetailsServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) {

        Customer customer = repository.findByPhone(phone).orElseThrow(() ->
                new NotFoundException(String.format("Customer does not exist, phone: %s", phone)));

        return org.springframework.security.core.userdetails.User.builder()
                .username(customer.phoneNumber())
                .password(customer.password())
                .build();
    }
}
