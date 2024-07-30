package com.iroegbulam.princewill.mecash.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepositRequest(@Digits(fraction = 2,integer = Integer.MAX_VALUE) double amount,
                             @NotBlank(message = "account number cannot be empty") @Digits(integer = 10,fraction = 0,message ="account number must be 10 digits long") @Size(min = 10, max = 10,message = "account number must be 10 digits") String accountNumber,
                             @NotBlank(message = "Account currency cannot be blank") @Size(min = 3, max = 3, message = "Please enter 3 character currency code")String currency) {
}
