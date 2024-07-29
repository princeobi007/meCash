package com.iroegbulam.princewill.mecash.service;

import com.iroegbulam.princewill.mecash.config.auth.TokenProvider;
import com.iroegbulam.princewill.mecash.domain.Customer;
import com.iroegbulam.princewill.mecash.domain.User;
import com.iroegbulam.princewill.mecash.dto.request.CustomerRegistration;
import com.iroegbulam.princewill.mecash.dto.request.LoginRequest;
import com.iroegbulam.princewill.mecash.enums.UserRole;
import com.iroegbulam.princewill.mecash.exception.DuplicateException;
import com.iroegbulam.princewill.mecash.repository.CustomerRepository;
import com.iroegbulam.princewill.mecash.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenProvider tokenProvider;

    private AuthService sut;

    Given given = new Given();
    When when = new When();

    @BeforeEach
    void setUp() {
        sut = new AuthService(userRepository,customerRepository,authenticationManager,tokenProvider);
    }

    @Nested
    class SignUp{
        @Test
        void duplicate_exception_is_thrown_for_duplicate_user(){
            var request = given.customerRegistration();
            given.user_is_duplicate_user();
            assertThatExceptionOfType(DuplicateException.class).isThrownBy(() -> when.signUp(request));
        }
        @Test
        void duplicate_exception_is_thrown_for_duplicate_customer(){
            var request = given.customerRegistration();
            given.user_is_not_found();
            given.customer_is_duplicate();
            assertThatExceptionOfType(DuplicateException.class).isThrownBy(() -> when.signUp(request));

        }

        @Test
        void signup_happy_path(){
            var request = given.customerRegistration();
            given.user_is_not_found();
            given.customer_is_not_found();
            assertThatCode(() -> when.signUp(request)).doesNotThrowAnyException();
        }
    }

    @Nested
    class Login{
        @Test
        void bad_credentials_exception_is_thrown(){
           var loginRequest = given.loginRequest();
           given.authentication_manager_fails();
            assertThatExceptionOfType(BadCredentialsException.class).isThrownBy(() -> when.login(loginRequest));
        }

        @Test
        void login_happy_path(){
            var loginRequest = given.loginRequest();
            given.authentication_manager_pass();
            given.token_provider_success();
            assertThatCode(() -> when.login(loginRequest)).doesNotThrowAnyException();
        }


    }


    class Given{

        public CustomerRegistration customerRegistration(){
            return new CustomerRegistration("08188736817",
                    "princneobi007@gmail.com", "testP@55", LocalDate.of(1999,6,23),
                    "Princewill", "Xander","Cage" ,"12345678901","12345678901");
        }

        public void user_is_duplicate_user() {
            Mockito.when(userRepository.findByLogin(Mockito.anyString())).thenReturn(Optional.of(new User("08188736817","testpass",UserRole.USER)));
        }

        public void user_is_not_found(){
            Mockito.when(userRepository.findByLogin(Mockito.anyString())).thenReturn(Optional.empty());
        }

        public void customer_is_not_found(){
            Mockito.when(customerRepository.findByPhoneNumber(Mockito.anyString())).thenReturn(Optional.empty());
        }

        public void customer_is_duplicate(){
            Mockito.when(customerRepository.findByPhoneNumber(Mockito.anyString())).thenReturn(Optional.of(new Customer()));
        }

        public LoginRequest loginRequest() {
            return new LoginRequest("08188736817", "testpass");
        }

        public void authentication_manager_fails() {
            Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new UsernameNotFoundException("not found"));
        }

        public void authentication_manager_pass() {
            Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return List.of(new SimpleGrantedAuthority("USER_ROLE"));
                }

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getDetails() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return null;
                }

                @Override
                public boolean isAuthenticated() {
                    return true;
                }

                @Override
                public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

                }

                @Override
                public String getName() {
                    return "test";
                }
            });
        }

        public void token_provider_success() {
            Mockito.when(tokenProvider.generateAccessToken(Mockito.any())).thenReturn("y85yt8y5y5y5y858");
        }
    }

    class When{

        public void signUp(CustomerRegistration request) {
            sut.signup(request);
        }

        public void login(LoginRequest loginRequest) {
            sut.login(loginRequest);
        }
    }
}