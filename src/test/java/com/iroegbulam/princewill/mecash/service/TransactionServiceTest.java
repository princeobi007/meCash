package com.iroegbulam.princewill.mecash.service;

import com.iroegbulam.princewill.mecash.domain.Account;
import com.iroegbulam.princewill.mecash.domain.Currency;
import com.iroegbulam.princewill.mecash.domain.Customer;
import com.iroegbulam.princewill.mecash.domain.User;
import com.iroegbulam.princewill.mecash.dto.request.DepositRequest;
import com.iroegbulam.princewill.mecash.dto.request.TransferRequest;
import com.iroegbulam.princewill.mecash.dto.request.WithdrawalRequest;
import com.iroegbulam.princewill.mecash.enums.UserRole;
import com.iroegbulam.princewill.mecash.exception.AccessDeniedException;
import com.iroegbulam.princewill.mecash.exception.NotFoundException;
import com.iroegbulam.princewill.mecash.exception.TransactionException;
import com.iroegbulam.princewill.mecash.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:test.properties")
class TransactionServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private MeCashService meCashService;

    private TransactionService sut;

    Given given = new Given();
    When when = new When();

    @BeforeEach
    void setUp() {
        sut = new TransactionService(accountRepository,transactionRepository,currencyRepository,userRepository,customerRepository,meCashService);
    }

    @Nested
    class Deposit{
        @Test
        void not_found_exception_thrown_when_beneficiary_not_found(){
            var request = given.depositRequest();
            given.beneficiaryAccountNotFound();
            assertThatExceptionOfType(NotFoundException.class).isThrownBy(()->when.deposit(request));
        }
        @Test
        void transaction_exception_thrown_when_request_currency_is_not_account_currency(){
            var request = given.depositRequest();
            given.beneficiaryUSDAccountFound();
            given.requestCurrencyIsNGN();
            assertThatExceptionOfType(TransactionException.class).isThrownBy(()->when.deposit(request));
        }

    }

    @Nested
    class Withdraw{

        @Test
        void not_found_exception_thrown_when_debit_account_not_found() {
            var request = given.withdrawalRequest();
            given.debitAccountNotFound();
            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> when.withdraw(request));
        }

        @Test
        void transaction_exception_thrown_when_request_currency_is_not_account_currency() {
            var request = given.usdWithdrawalRequest();
            given.debitUSDAccountFound();
            given.requestCurrencyIsNGN();
            assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> when.withdraw(request));
        }

        @Test
        void transaction_exception_thrown_when_insufficient_balance() {
            var request = given.withdrawalRequest();
            given.debitNGNAccountFound();
            given.requestCurrencyIsNGN();
            given.insufficientBalance();
            assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> when.withdraw(request));
        }
    }

    @Nested
    class Transfer{

        @Test
        void not_found_exception_thrown_when_debit_account_not_found() {
            var request = given.transferRequest();
            given.debitAccountNotFound();
            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> when.transfer(request));
        }

        @Test
        void not_found_exception_thrown_when_credit_account_not_found() {
            var request = given.transferRequest();
            given.debitNGNAccountFound();
            given.creditAccountNotFound();
            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> when.transfer(request));
        }

        @Test
        void access_denied_exception_thrown_when_not_signatory() {
            var request = given.transferRequest();
            given.debitNGNAccountFound();
            given.creditNGNAccountFound();
            given.currentUserNotSignatory();
            assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(() -> when.transfer(request));
        }

        @Test
        void transaction_exception_thrown_when_account_currencies_mismatch() {
            var request = given.usdTransferRequest();
            given.debitUSDAccountFound();
            given.creditNGNAccountFound();
            given.currentUserIsSignatory();
            assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> when.transfer(request));
        }

        @Test
        void transaction_exception_thrown_when_insufficient_balance() {
            var request = given.transferRequest();
            given.debitNGNAccountFound();
            given.creditNGNAccountFound();
            given.currentUserIsSignatory();
            given.insufficientBalance();
            assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> when.transfer(request));
        }

        @Test
        void transfer_happy_path() {
            var request = given.transferRequest();
            given.debitNGNAccountFound();
            given.creditNGNAccountFound();
            given.currentUserIsSignatory();
            given.sufficientBalance();
            given.debitAccountUpdatesBalance();
            assertThatCode(() -> when.transfer(request)).doesNotThrowAnyException();
        }

    }

    class Given {
        private final Account depositAccountUSD = new Account();
        private final Account depositAccountNGN = new Account();
        private final Account tillAccountNGN = new Account();
        private final Currency ngnCurrency = new Currency();
        private final Currency usdCurrency = new Currency();
        private final Customer customer = new Customer();
        {
            ngnCurrency.setId(1L);
            usdCurrency.setId(2L);
            ngnCurrency.setCode("NGN");
            usdCurrency.setCode("USD");
            customer.setId(1L);

            depositAccountUSD.setAccountNumber("1000000003");
            depositAccountUSD.setAccountName("ACCOUNT_NAME_USD");
            depositAccountUSD.setAccountCurrency(usdCurrency);
            depositAccountUSD.setSignatories(new ArrayList<>());
            depositAccountUSD.getSignatories().add(customer);



            depositAccountNGN.setId(1L);
            depositAccountNGN.setAccountNumber("1000000001");
            depositAccountNGN.setAccountName("ACCOUNT_NAME_NGN");
            depositAccountNGN.setAccountCurrency(ngnCurrency);
            depositAccountNGN.setSignatories(new ArrayList<>());
            depositAccountNGN.getSignatories().add(customer);

            tillAccountNGN.setAccountNumber("2000000002");
            tillAccountNGN.setAccountName("TILL ACCOUNT_NAME");
            tillAccountNGN.setAccountCurrency(ngnCurrency);
        }

        public DepositRequest depositRequest() {
            return new DepositRequest(10000.70, "1000000001", "NGN");
        }

        public WithdrawalRequest withdrawalRequest() {
            return new WithdrawalRequest(10000.70, "1000000001", "NGN");
        }

        public WithdrawalRequest usdWithdrawalRequest() {
            return new WithdrawalRequest(10000.70, "1000000003", "NGN");
        }

        public TransferRequest transferRequest() {
            return new TransferRequest("1000000001",  "2000000002", 10000.70, "Test transfer");
        }

        public TransferRequest usdTransferRequest(){
            return new TransferRequest("1000000003",  "2000000002", 10000.70, "Test transfer");

        }

        public void beneficiaryAccountNotFound() {
            Mockito.when(accountRepository.findByAccountNumber(Mockito.anyString())).thenReturn(Optional.empty());
        }

        public void beneficiaryUSDAccountFound() {
            Mockito.when(accountRepository.findByAccountNumber(Mockito.anyString())).thenReturn(Optional.of(depositAccountUSD));
        }

        public void requestCurrencyIsNGN() {
            Mockito.when(currencyRepository.findByCode("NGN")).thenReturn(Optional.of(ngnCurrency));
        }

        public void debitAccountNotFound() {
            Mockito.when(accountRepository.findByAccountNumber("1000000001")).thenReturn(Optional.empty());
        }

        public void debitUSDAccountFound() {
            Mockito.lenient().when(accountRepository.findByAccountNumber("1000000003")).thenReturn(Optional.of(depositAccountUSD));
        }

        public void debitNGNAccountFound() {
            Mockito.lenient().when(accountRepository.findByAccountNumber("1000000001")).thenReturn(Optional.of(depositAccountNGN));
        }

        public void creditAccountNotFound() {
            Mockito.when(accountRepository.findByAccountNumber("2000000002")).thenReturn(Optional.empty());
        }

        public void creditNGNAccountFound() {
            Mockito.lenient().when(accountRepository.findByAccountNumber("2000000002")).thenReturn(Optional.of(tillAccountNGN));
        }

        public void currentUserNotSignatory() {
            Mockito.when(meCashService.getCurrentUser()).thenReturn(new User("current_user", "password", UserRole.USER));
            Mockito.when(customerRepository.findByUser_Login(Mockito.anyString())).thenReturn(Optional.of(new Customer()));
        }

        public void currentUserIsSignatory() {
            var customer = new Customer();
            customer.setId(1L);
            var account = new Account();
            account.setSignatories(new ArrayList<>()); // Initialize the signatories list
            account.getSignatories().add(customer);
            Mockito.when(meCashService.getCurrentUser()).thenReturn(new User("current_user", "password", UserRole.USER));
            Mockito.when(customerRepository.findByUser_Login(Mockito.anyString())).thenReturn(Optional.of(customer));
            Mockito.lenient().when(accountRepository.findByAccountNumber("1000000001")).thenReturn(Optional.of(account));
        }

        public void insufficientBalance() {
            var account = depositAccountNGN;
            account.setAvailableBalance(5000.0);
            Mockito.lenient().when(accountRepository.findByAccountNumber("1000000001")).thenReturn(Optional.of(account));
        }

        public void sufficientBalance() {
            var account = depositAccountNGN;
            account.setAvailableBalance(15000.0);
            Mockito.lenient().when(accountRepository.findByAccountNumber("1000000001")).thenReturn(Optional.of(account));
        }

        public void debitAccountUpdatesBalance() {
            Mockito.when(accountRepository.findById(Mockito.any())).thenReturn(Optional.of(depositAccountNGN));
        }
    }


    class When {

        public void deposit(DepositRequest request) {
            sut.deposit(request);
        }

        public void withdraw(WithdrawalRequest request) {
            sut.withdraw(request);
        }

        public void transfer(TransferRequest request) {
            sut.transfer(request);
        }
    }
}