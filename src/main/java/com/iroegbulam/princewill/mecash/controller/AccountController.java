package com.iroegbulam.princewill.mecash.controller;


import com.iroegbulam.princewill.mecash.dto.request.AccountCreationRequest;
import com.iroegbulam.princewill.mecash.dto.response.AccountBalanceResponse;
import com.iroegbulam.princewill.mecash.dto.response.AccountCreationResponse;
import com.iroegbulam.princewill.mecash.dto.response.ApiErrorResponse;
import com.iroegbulam.princewill.mecash.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "create account")
    @ApiResponse(responseCode = "201")
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping()
    public ResponseEntity<AccountCreationResponse> createAccount(@Valid @RequestBody AccountCreationRequest accountCreationRequest) {
        return ResponseEntity.ok( accountService.createAccount(accountCreationRequest));
    }

    @Operation(summary = "get all user account balance")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/all")
    public ResponseEntity<AccountBalanceResponse> getBalance() {
        AccountBalanceResponse response = accountService.getAccountBalance();
        log.info("{}",response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "get an account balance")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping()
    public ResponseEntity<AccountBalanceResponse.AccountDto> getAccountBalance(@RequestParam String accountNumber) {
        return ResponseEntity.ok(accountService.getAnAccountBalance(accountNumber));
    }
}
