package com.iroegbulam.princewill.mecash.service;

import com.iroegbulam.princewill.mecash.domain.*;
import com.iroegbulam.princewill.mecash.dto.request.DepositRequest;
import com.iroegbulam.princewill.mecash.dto.request.TransferRequest;
import com.iroegbulam.princewill.mecash.dto.request.WithdrawalRequest;
import com.iroegbulam.princewill.mecash.dto.response.DepositResponse;
import com.iroegbulam.princewill.mecash.dto.response.TransferResponse;
import com.iroegbulam.princewill.mecash.dto.response.WithdrawlResponse;
import com.iroegbulam.princewill.mecash.enums.*;
import com.iroegbulam.princewill.mecash.exception.AccessDeniedException;
import com.iroegbulam.princewill.mecash.exception.CurrencyNotSupportedException;
import com.iroegbulam.princewill.mecash.exception.NotFoundException;
import com.iroegbulam.princewill.mecash.exception.TransactionException;
import com.iroegbulam.princewill.mecash.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {
    private static final String CURRENCY_MISMATCH_MESSAGE = "Currency mismatch. Request currency: %s beneficiary account currency %s";
    private static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance. Request amount %f  available amount %f";
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final MeCashService meCashService;
    @Value("${mecash.till.account.number.usd}")
    private String usdTillAccountNumber;
    @Value("${mecash.till.account.number.gbp}")
    private String gdpTillAccountNumber;
    @Value("${mecash.till.account.number.euro}")
    private String euroTillAccountNumber;
    @Value("${mecash.till.account.number.yen}")
    private String yenTillAccountNumber;
    @Value("${mecash.till.account.number.yuan}")
    private String yuanTillAccountNumber;
    @Value("${mecash.till.account.number.ngn}")
    private String ngnTillAccount;
    @Value("${mecash.admin.user.login}")
    private String adminLogin;
    @Value("${mecash.admin.user.password}")
    private String adminPass;

    @Transactional
    public DepositResponse deposit(DepositRequest request) {
        //validate account to credit
        var beneficiaryAccount = getCustomerAccount(request.accountNumber());

        if (!beneficiaryAccount.getAccountCurrency().equals(getCurrency(request.currency()))) {
            throw new TransactionException(String.format(CURRENCY_MISMATCH_MESSAGE, request.currency(), beneficiaryAccount.getAccountCurrency().toString()));
        }
        synchronized (this) {
            var transactionRef = generateTransactionRef();
            var tillAccount = getTillAccount(request.currency());
            var narration = "Cash deposit";
            //credit till account
            creditAccount(tillAccount, request.amount(), TransactionCategory.DEPOSIT, transactionRef, narration);
            //debit till account
            debitAccount(tillAccount, request.amount(), TransactionCategory.DEPOSIT, transactionRef, narration);
            //credit beneficiary
            creditAccount(beneficiaryAccount, request.amount(), TransactionCategory.DEPOSIT, transactionRef, narration);
        }

        return new DepositResponse(TransactionStatus.SUCCESS, accountRepository.findById(beneficiaryAccount.getId()).orElseThrow().getAvailableBalance());
    }

    @Transactional
    public WithdrawlResponse withdraw(WithdrawalRequest request) {
        //validate account to debit
        var debitAccount = getCustomerAccount(request.accountNumber());
        if (!debitAccount.getAccountCurrency().equals(getCurrency(request.currency()))) {
            throw new TransactionException(String.format(CURRENCY_MISMATCH_MESSAGE, request.currency(), debitAccount.getAccountCurrency().toString()));
        }

        synchronized (this) {
            var transactionRef = generateTransactionRef();
            var tillAccount = getTillAccount(request.currency());

            if (debitAccount.getAvailableBalance() < request.amount()) {
                throw new TransactionException(String.format(INSUFFICIENT_BALANCE_MESSAGE, request.amount(), debitAccount.getAvailableBalance()));
            }

            var narration = "cash withdrawal";
            //debit customer account
            debitAccount(debitAccount, request.amount(), TransactionCategory.WITHDRAWAL, transactionRef, narration);
            //credit till account
            creditAccount(tillAccount, request.amount(), TransactionCategory.WITHDRAWAL, transactionRef, narration);
            //debit till account
            debitAccount(tillAccount, request.amount(), TransactionCategory.WITHDRAWAL, transactionRef, narration);
        }
        return new WithdrawlResponse(TransactionStatus.SUCCESS, accountRepository.findById(debitAccount.getId()).orElseThrow().getAvailableBalance());
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        //validate debitAccount and credit account
        var debitAccount = getCustomerAccount(request.debitAccount());
        var creditAccount = getCustomerAccount(request.creditAccount());

        //validate current user is a signatory to account
        var currentUser = meCashService.getCurrentUser();
        var customer = customerRepository.findByUser_Login(currentUser.getLogin()).orElseThrow(() -> new NotFoundException(String.format("No customer profile for user: %s", currentUser.getLogin())));
        if (!debitAccount.getSignatories().contains(customer)) {
            throw new AccessDeniedException(String.format("You are not a signatory to account %s", debitAccount.getAccountNumber()));
        }

        //validate account are same currency
        if (!debitAccount.getAccountCurrency().equals(creditAccount.getAccountCurrency())) {
            throw new TransactionException(String.format(CURRENCY_MISMATCH_MESSAGE, creditAccount.getAccountCurrency().toString(), debitAccount.getAccountCurrency().toString()));

        }
        synchronized (this) {
            var transactionRef = generateTransactionRef();
            //validate the debit account has sufficient balance
            if (debitAccount.getAvailableBalance() < request.amount()) {
                throw new TransactionException(String.format(INSUFFICIENT_BALANCE_MESSAGE, request.amount(), debitAccount.getAvailableBalance()));
            }

            //debit account
            debitAccount(debitAccount, request.amount(), TransactionCategory.TRANSFER, transactionRef, request.narration());
            //credit beneficiary
            creditAccount(creditAccount, request.amount(), TransactionCategory.TRANSFER, transactionRef, request.narration());
        }

        return new TransferResponse(TransactionStatus.SUCCESS, accountRepository.findById(debitAccount.getId()).orElseThrow().getAvailableBalance());
    }

    private void creditAccount(Account account, double amount, TransactionCategory transactionCategory, String transactionRef, String narration) {
        //create transaction
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransactionCategory(transactionCategory);
        transaction.setAmount(amount);
        transaction.setCurrency(account.getAccountCurrency());
        transaction.setTransactionRef(transactionRef);
        transaction.setCreatedBy(meCashService.getCurrentUser());
        transaction.setNarration(narration);
        transactionRepository.save(transaction);

        //update account balance
        account.setAvailableBalance(account.getAvailableBalance() + amount);
        accountRepository.save(account);
    }

    private void debitAccount(Account account, double amount, TransactionCategory transactionCategory, String transactionRef, String narration) {
        //create transaction
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setTransactionCategory(transactionCategory);
        transaction.setAmount(amount);
        transaction.setCurrency(account.getAccountCurrency());
        transaction.setTransactionRef(transactionRef);
        transaction.setNarration(narration);
        transaction.setCreatedBy(meCashService.getCurrentUser());
        transactionRepository.save(transaction);

        //update account balance
        account.setAvailableBalance(account.getAvailableBalance() - amount);
        accountRepository.save(account);
    }

    private Account getCustomerAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new NotFoundException(String.format("%s account not found", accountNumber)));
    }


    private Account getTillAccount(String currencyCode) {
        String tillAccount;
        switch (currencyCode) {
            case "USD" -> tillAccount = usdTillAccountNumber;
            case "EUR" -> tillAccount = euroTillAccountNumber;
            case "GBP" -> tillAccount = gdpTillAccountNumber;
            case "JPY" -> tillAccount = yenTillAccountNumber;
            case "CNY" -> tillAccount = yuanTillAccountNumber;
            case "NGN" -> tillAccount = ngnTillAccount;
            default -> throw new CurrencyNotSupportedException(String.format("%s not supported", currencyCode));

        }
        return accountRepository.findByAccountNumber(tillAccount).orElse(createTillAccount(currencyCode));
    }

    private Account createTillAccount(String currencyCode) {
        var currency = getCurrency(currencyCode);
        var adminUser = getAdminUser();

        Account account = new Account();
        account.setAccountType(AccountType.TILL);
        account.setCreatedBy(adminUser);
        account.setAccountCurrency(currency);

        switch (currencyCode) {
            case "USD" -> {
                account.setAccountNumber(usdTillAccountNumber);
                account.setAccountName("USD TILL ACCOUNT");
            }
            case "EUR" -> {
                account.setAccountNumber(euroTillAccountNumber);
                account.setAccountName("EURO TILL ACCOUNT");
            }
            case "GBP" -> {
                account.setAccountNumber(gdpTillAccountNumber);
                account.setAccountName("GBP TILL ACCOUNT");
            }
            case "JPY" -> {
                account.setAccountNumber(yenTillAccountNumber);
                account.setAccountName("JPY TILL ACCOUNT");
            }
            case "CNY" -> {
                account.setAccountNumber(yuanTillAccountNumber);
                account.setAccountName("CNY TILL ACCOUNT");
            }
            case "NGN" -> {
                account.setAccountNumber(ngnTillAccount);
                account.setAccountName("NGN TILL ACCOUNT");
            }
            default -> throw new CurrencyNotSupportedException(String.format("%s not supported", currencyCode));
        }

        return accountRepository.save(account);
    }

    private User getAdminUser() {
        return (User) userRepository.findByLogin(adminLogin).orElse(userRepository.save(new User(adminLogin, adminPass, UserRole.ADMIN)));
    }

    private Currency getCurrency(String currencyCode) {
        return currencyRepository.findByCode(currencyCode).orElseThrow(() -> new CurrencyNotSupportedException(String.format("%s Currency  not supported", currencyCode)));
    }

    private String generateTransactionRef() {
        return UUID.randomUUID().toString();
    }
}
