package com.iroegbulam.princewill.mecash.service;

import com.iroegbulam.princewill.mecash.domain.Account;
import com.iroegbulam.princewill.mecash.domain.Currency;
import com.iroegbulam.princewill.mecash.domain.Customer;
import com.iroegbulam.princewill.mecash.domain.User;
import com.iroegbulam.princewill.mecash.dto.request.AccountCreationRequest;
import com.iroegbulam.princewill.mecash.enums.AccountType;
import com.iroegbulam.princewill.mecash.enums.UserRole;
import com.iroegbulam.princewill.mecash.exception.AccountCreationException;
import com.iroegbulam.princewill.mecash.exception.CurrencyNotSupportedException;
import com.iroegbulam.princewill.mecash.exception.DuplicateException;
import com.iroegbulam.princewill.mecash.exception.NotFoundException;
import com.iroegbulam.princewill.mecash.repository.AccountRepository;
import com.iroegbulam.princewill.mecash.repository.CurrencyRepository;
import com.iroegbulam.princewill.mecash.repository.CustomerRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private MeCashService meCashService;
    @Mock
    EntityManager entityManager;
    AccountService sut;

    Given given = new Given();
    When when = new When();



    @BeforeEach
    void setUp() {
        sut = new AccountService(accountRepository,customerRepository,currencyRepository,meCashService,entityManager);
    }

    @Nested
    class IndividualAccountAndBusinessAccount{
        @Test
        void unsupported_currency_should_throw_currency_not_supported_exception(){
            var request = given.unsupported_currency_request();
            given.currency_not_supported();
            assertThatExceptionOfType(CurrencyNotSupportedException.class).isThrownBy(()-> when.createAccount(request));
        }

        @Test
        void not_found_exception_is_thrown_when_signatory_does_not_have_customer_profile(){
            var request = given.invalid_signatory_request();
            given.currency_supported();
            given.customer_profile_not_found();
            assertThatExceptionOfType(NotFoundException.class).isThrownBy(()->when.createAccount(request));
        }

        @Test
        void not_found_exception_is_thrown_when_logged_in_user_does_not_have_customer_profile(){
            var request = given.validRequest();
            given.currency_supported();
            given.signatory_customer_profile_found();
            given.logged_in_user();
            assertThatExceptionOfType(NotFoundException.class).isThrownBy(()->when.createAccount(request));
        }

        @Test
        void duplicate_exception_is_thrown_when_creating_account(){
            var request = given.validRequest();
            given.currency_supported();
            given.signatory_customer_profile_found();
            given.logged_in_user_profile_found();
            given.logged_in_user();
            given.duplicate_exists();
            assertThatExceptionOfType(DuplicateException.class).isThrownBy(()->when.createAccount(request));
        }

        @Test
        void create_account_happy_path(){
            var request = given.validRequest();
            given.currency_supported();
            given.signatory_customer_profile_found();
            given.logged_in_user_profile_found();
            given.logged_in_user();
            given.no_duplicate();
            given.valid_sequence();
            assertThatCode(()->when.createAccount(request)).doesNotThrowAnyException();

        }
    }

    @Nested
    class LlcAccount{

        @Test
        void account_creation_exception_is_thrown_when_one_signatory_for_llc(){
            var request =given.one_signatory_for_llc_request();
            assertThatExceptionOfType(AccountCreationException.class).isThrownBy(()->when.createAccount(request));
        }
        @Test
        void account_creation_exception_is_thrown_when_no_registration(){
            var request =given.no_registration_request();
            assertThatExceptionOfType(AccountCreationException.class).isThrownBy(()->when.createAccount(request));
        }

    }



    class Given{

        public AccountCreationRequest unsupported_currency_request() {
            return new AccountCreationRequest(AccountType.I,"TEST ACCOUNT","ZYR", "",List.of());
        }

        public void currency_not_supported() {
            Mockito.when(currencyRepository.findByCode(Mockito.anyString())).thenReturn(Optional.empty());
        }

        public AccountCreationRequest invalid_signatory_request() {
            return new AccountCreationRequest(AccountType.I, "Test Account", "USD", "",List.of("testCustomerID"));
        }

        public void currency_supported() {
            Mockito.when(currencyRepository.findByCode(Mockito.anyString())).thenReturn(Optional.of(new Currency()));
        }

        public void customer_profile_not_found() {
            Mockito.when(customerRepository.findByCustomerId(Mockito.anyString())).thenReturn(Optional.empty());
        }

        public AccountCreationRequest validRequest() {
            return new AccountCreationRequest(AccountType.I,"TEST ACCOUNT", "USD", "",List.of("TEST_SIGNATORY"));
        }

        public void signatory_customer_profile_found() {
            Mockito.when(customerRepository.findByCustomerId("TEST_SIGNATORY")).thenReturn(Optional.of(new Customer()));
        }

        public void logged_in_user() {
            Mockito.when(meCashService.getCurrentUser()).thenReturn(new User("LoggedInUser", "test@Pa55", UserRole.USER));
        }

        public void logged_in_user_profile_found() {
            Customer customer = new Customer();
            customer.setCustomerId("TEST_ID");
            Mockito.when(customerRepository.findByUser_Login("LoggedInUser")).thenReturn(Optional.of(customer));
        }

        public void duplicate_exists() {
            Mockito.when(accountRepository.findByAccountNameIgnoreCaseAndAccountCurrency_CodeIgnoreCaseAndSignatories_CustomerIdInAndAccountType(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(List.of(new Account()));
        }

        public void no_duplicate() {
            Mockito.when(accountRepository.findByAccountNameIgnoreCaseAndAccountCurrency_CodeIgnoreCaseAndSignatories_CustomerIdInAndAccountType(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(List.of());
        }

        public void valid_sequence() {
            Mockito.when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(new Query() {
                @Override
                public List getResultList() {
                    return null;
                }

                @Override
                public Object getSingleResult() {
                    return 1L;
                }

                @Override
                public int executeUpdate() {
                    return 0;
                }

                @Override
                public Query setMaxResults(int maxResult) {
                    return null;
                }

                @Override
                public int getMaxResults() {
                    return 0;
                }

                @Override
                public Query setFirstResult(int startPosition) {
                    return null;
                }

                @Override
                public int getFirstResult() {
                    return 0;
                }

                @Override
                public Query setHint(String hintName, Object value) {
                    return null;
                }

                @Override
                public Map<String, Object> getHints() {
                    return null;
                }

                @Override
                public <T> Query setParameter(Parameter<T> param, T value) {
                    return null;
                }

                @Override
                public Query setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
                    return null;
                }

                @Override
                public Query setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
                    return null;
                }

                @Override
                public Query setParameter(String name, Object value) {
                    return null;
                }

                @Override
                public Query setParameter(String name, Calendar value, TemporalType temporalType) {
                    return null;
                }

                @Override
                public Query setParameter(String name, Date value, TemporalType temporalType) {
                    return null;
                }

                @Override
                public Query setParameter(int position, Object value) {
                    return null;
                }

                @Override
                public Query setParameter(int position, Calendar value, TemporalType temporalType) {
                    return null;
                }

                @Override
                public Query setParameter(int position, Date value, TemporalType temporalType) {
                    return null;
                }

                @Override
                public Set<Parameter<?>> getParameters() {
                    return null;
                }

                @Override
                public Parameter<?> getParameter(String name) {
                    return null;
                }

                @Override
                public <T> Parameter<T> getParameter(String name, Class<T> type) {
                    return null;
                }

                @Override
                public Parameter<?> getParameter(int position) {
                    return null;
                }

                @Override
                public <T> Parameter<T> getParameter(int position, Class<T> type) {
                    return null;
                }

                @Override
                public boolean isBound(Parameter<?> param) {
                    return false;
                }

                @Override
                public <T> T getParameterValue(Parameter<T> param) {
                    return null;
                }

                @Override
                public Object getParameterValue(String name) {
                    return null;
                }

                @Override
                public Object getParameterValue(int position) {
                    return null;
                }

                @Override
                public Query setFlushMode(FlushModeType flushMode) {
                    return null;
                }

                @Override
                public FlushModeType getFlushMode() {
                    return null;
                }

                @Override
                public Query setLockMode(LockModeType lockMode) {
                    return null;
                }

                @Override
                public LockModeType getLockMode() {
                    return null;
                }

                @Override
                public <T> T unwrap(Class<T> cls) {
                    return null;
                }
            });
        }

        public AccountCreationRequest one_signatory_for_llc_request() {
            return new AccountCreationRequest(AccountType.LLC,"NAME","USD","TEST_RC",List.of());
        }

        public AccountCreationRequest no_registration_request() {
            return new AccountCreationRequest(AccountType.LLC,"NAME","USD","",List.of("TESt"));
        }
    }

    class When{

        public void createAccount(AccountCreationRequest request) {
            sut.createAccount(request);
        }
    }

}