package com.iroegbulam.princewill.mecash.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iroegbulam.princewill.mecash.config.AuthConfig;
import com.iroegbulam.princewill.mecash.config.auth.SecurityFilter;
import com.iroegbulam.princewill.mecash.config.auth.TokenProvider;
import com.iroegbulam.princewill.mecash.dto.CustomerRegistration;
import com.iroegbulam.princewill.mecash.repository.CustomerRepository;
import com.iroegbulam.princewill.mecash.repository.UserRepository;
import com.iroegbulam.princewill.mecash.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {AuthenticationController.class})
@Import(AuthConfig.class)
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private AuthenticationManager manager;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        SecurityFilter securityFilter = new SecurityFilter(tokenProvider,userRepository);
        authService = new AuthService(userRepository,customerRepository,manager,tokenProvider);
    }

    @Test
    void signup_ShouldReturn201_WhenValidRequest() throws Exception {
        CustomerRegistration customerRegistration = new CustomerRegistration("08188736817",
                "princneobi007@gmail.com", "testP@55", LocalDate.of(1999,6,23),
                "Princewill", "Xander","Cage" ,"12345678901","12345678901");


        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRegistration)))
                .andExpect(status().isCreated());
    }
}