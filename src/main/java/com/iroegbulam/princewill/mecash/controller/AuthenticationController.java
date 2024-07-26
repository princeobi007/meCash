package com.iroegbulam.princewill.mecash.controller;

import com.iroegbulam.princewill.mecash.dto.ApiErrorResponse;
import com.iroegbulam.princewill.mecash.dto.CustomerRegistration;
import com.iroegbulam.princewill.mecash.dto.LoginRequest;
import com.iroegbulam.princewill.mecash.dto.LoginResponse;
import com.iroegbulam.princewill.mecash.helper.JwtHelper;
import com.iroegbulam.princewill.mecash.service.CustomerService;
import com.iroegbulam.princewill.mecash.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final LoginService loginService;

    public AuthenticationController(AuthenticationManager authenticationManager, CustomerService userService, LoginService loginService) {
        this.authenticationManager = authenticationManager;
        this.customerService = userService;
        this.loginService = loginService;
    }
    @Operation(summary = "Signup user")
    @ApiResponse(responseCode = "201")
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody CustomerRegistration customerRegistration) {
        customerService.signup(customerRegistration);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Authenticate user and return token")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.phone(), request.password()));
        } catch (BadCredentialsException e) {
            loginService.addLoginAttempt(request.phone(), false);
            throw e;
        }

        String token = JwtHelper.generateToken(request.phone());
        loginService.addLoginAttempt(request.phone(), true);
        return ResponseEntity.ok(new LoginResponse(request.phone(), token));
    }
}
