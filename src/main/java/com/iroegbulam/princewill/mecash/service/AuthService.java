package com.iroegbulam.princewill.mecash.service;

import com.iroegbulam.princewill.mecash.config.auth.TokenProvider;
import com.iroegbulam.princewill.mecash.domain.Customer;
import com.iroegbulam.princewill.mecash.domain.User;
import com.iroegbulam.princewill.mecash.dto.request.CustomerRegistration;
import com.iroegbulam.princewill.mecash.dto.request.LoginRequest;
import com.iroegbulam.princewill.mecash.dto.response.LoginResponse;
import com.iroegbulam.princewill.mecash.enums.UserRole;
import com.iroegbulam.princewill.mecash.exception.DuplicateException;
import com.iroegbulam.princewill.mecash.repository.CustomerRepository;
import com.iroegbulam.princewill.mecash.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthService implements UserDetailsService {
    private final UserRepository repository;
    private final CustomerRepository customerRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public AuthService(UserRepository repository, CustomerRepository customerRepository, @Lazy AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.repository = repository;
        this.customerRepository = customerRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) {
        return repository.findByLogin(phone).orElseThrow(()->new BadCredentialsException("Could not load username"));
    }

    @Transactional
    public void signup(CustomerRegistration request) {
        String phoneNumber = request.phoneNumber();
        if (repository.findByLogin(phoneNumber).isPresent()) {
            throw new DuplicateException(String.format("User with the phone number '%s' already exists.", phoneNumber));
        }

        Optional<Customer> existingUser = customerRepository.findByPhoneNumber(phoneNumber);
        if (existingUser.isPresent()) {
            throw new DuplicateException(String.format("Customer with the phone number '%s' already exists.", phoneNumber));
        }

        // create User object
        String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());
        User newUser = new User(request.phoneNumber(), encryptedPassword, UserRole.USER);
        var createdUser =  repository.save(newUser);

        //Create customer
        Customer newCustomer = new Customer();

        newCustomer.setFirstname(request.firstname());
        newCustomer.setLastname(request.lastName());
        newCustomer.setMiddleName(request.middleName());
        newCustomer.setPhoneNumber(request.phoneNumber());
        newCustomer.setDob(request.dateOfBirth());
        newCustomer.setEmail(request.email());
        newCustomer.setBvn(request.bvn());
        newCustomer.setNin(request.nin());
        newCustomer.setUser(createdUser);
        newCustomer.setCustomerId(generateCustomerId());

        customerRepository.saveAndFlush(newCustomer);
    }

    private String generateCustomerId() {
        return UUID.randomUUID().toString();
    }

    public LoginResponse login (LoginRequest loginRequest){
        try{
            var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.phone(), loginRequest.password());
            var authUser = authenticationManager.authenticate(usernamePassword);
            var accessToken = tokenProvider.generateAccessToken((User) authUser.getPrincipal());
            return new LoginResponse(loginRequest.phone(), accessToken);
        }catch (UsernameNotFoundException  e){
            throw new BadCredentialsException("could not find user for credentials");
        }
    }

}
