package com.iroegbulam.princewill.mecash.service;

import com.iroegbulam.princewill.mecash.domain.Account;
import com.iroegbulam.princewill.mecash.domain.Currency;
import com.iroegbulam.princewill.mecash.domain.Customer;
import com.iroegbulam.princewill.mecash.domain.User;
import com.iroegbulam.princewill.mecash.dto.request.AccountCreationRequest;
import com.iroegbulam.princewill.mecash.dto.response.AccountCreationResponse;
import com.iroegbulam.princewill.mecash.enums.AccountType;
import com.iroegbulam.princewill.mecash.exception.*;
import com.iroegbulam.princewill.mecash.repository.AccountRepository;
import com.iroegbulam.princewill.mecash.repository.CurrencyRepository;
import com.iroegbulam.princewill.mecash.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CurrencyRepository currencyRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public AccountCreationResponse createAccount(AccountCreationRequest request) {

        AccountCreationResponse response;
        if (request.accountType().equals(AccountType.I)) {
            response = new AccountCreationResponse(createGenericAccount(request));
        } else if (request.accountType().equals(AccountType.BN)){
            if(request.registrationNumber().isEmpty()){
                throw new AccountCreationException("BN must have a BN number ");
            }
            response = new AccountCreationResponse(createGenericAccount(request));
        }else if (request.accountType().equals(AccountType.LLC)) {
            if (request.signatories() == null || request.signatories().isEmpty()) {
                throw new AccountCreationException("LLC must have at lease 2 signatories, add at least one signatory ");
            }
            if (request.registrationNumber().isEmpty()){
                throw new AccountCreationException("LLC must have an RC number ");
            }
            response = new AccountCreationResponse(createGenericAccount(request));
        } else {
            throw new AccountCreationException(String.format("Unrecognised account type: %s", request.accountType()));
        }

        return response;
    }


    private String createGenericAccount(AccountCreationRequest request) {

        var currency = getCurrency(request);
        List<Customer> signatoryList = new ArrayList<>();

        List<String> signatories = new ArrayList<>();

        if (request.signatories() != null) {
            request.signatories().forEach(signatory -> signatoryList.add(customerRepository.findByCustomerId(signatory).orElseThrow(() -> new NotFoundException(String.format("No customer found for customerId: %s", signatory)))));


            signatories.addAll(request.signatories());
        }


        var currentUser = getCurrentUser();
        var userCustomer = getCustomer(currentUser);
        signatoryList.add(userCustomer);


        signatories.add(userCustomer.getCustomerId());

        //check for duplicate
        var dupAccount = accountRepository.findByAccountNameIgnoreCaseAndAccountCurrency_CodeIgnoreCaseAndSignatories_CustomerIdInAndAccountType
                (request.accountName(), request.currencyCode(), signatories, request.accountType());

        if (!dupAccount.isEmpty()) {
            throw new DuplicateException(String.format("An account with these parameters has been created. Account name : %s Account currency: %s", request.accountName().toUpperCase(), request.currencyCode().toUpperCase()));
        }


        Account account = new Account();
        account.setAccountCurrency(currency);
        account.setAccountName(request.accountName().toUpperCase());
        account.setAccountType(request.accountType());
        account.setCreatedBy(currentUser);
        account.setAccountNumber(generateAccountNumber());
        account.setSignatories(signatoryList);
        if(!request.registrationNumber().isEmpty()){
            account.setRegistrationNumber(request.registrationNumber());
        }
        accountRepository.saveAndFlush(account);


        return account.getAccountNumber();
    }

    private Customer getCustomer(User currentUser) {
        return customerRepository.findByUser_Login(currentUser.getLogin())
                .orElseThrow(() -> new NotFoundException("No customer profile found for logged in user"));
    }

    private Currency getCurrency(AccountCreationRequest request) {
        return currencyRepository.findByCode(request.currencyCode()).orElseThrow(() -> new CurrencyNotSupportedException(String.format("%s currency not supported", request.currencyCode())));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return (User) authentication.getPrincipal();
        }
        throw new AccessDeniedException("Kindly confirm user is logged in");
    }

    private String generateAccountNumber() {
        Long nextVal = (Long) entityManager.createNativeQuery("SELECT nextval('account_number_seq')").getSingleResult();
        return String.format("%010d", nextVal);
    }
}
