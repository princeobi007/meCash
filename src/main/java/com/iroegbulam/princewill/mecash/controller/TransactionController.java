package com.iroegbulam.princewill.mecash.controller;


import com.iroegbulam.princewill.mecash.dto.request.DepositRequest;
import com.iroegbulam.princewill.mecash.dto.request.TransferRequest;
import com.iroegbulam.princewill.mecash.dto.request.WithdrawalRequest;
import com.iroegbulam.princewill.mecash.dto.response.*;
import com.iroegbulam.princewill.mecash.service.TransactionService;
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
@RequestMapping(path = "/api/v1/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(summary = "deposit")
    @ApiResponse(responseCode = "201")
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PutMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(@Valid @RequestBody DepositRequest depositRequest) {
        return ResponseEntity.ok( transactionService.deposit(depositRequest));
    }

    @Operation(summary = "withdraw")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PutMapping("/withdraw")
    public ResponseEntity<WithdrawlResponse> withdraw(@Valid @RequestBody WithdrawalRequest withdrawalRequest) {
        return ResponseEntity.ok(transactionService.withdraw(withdrawalRequest));
    }

    @Operation(summary = "get an account balance")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PutMapping("/transfer")
    public ResponseEntity<TransferResponse> getAccountBalance(@Valid @RequestBody TransferRequest transferRequest) {
        return ResponseEntity.ok(transactionService.transfer(transferRequest));
    }
}
