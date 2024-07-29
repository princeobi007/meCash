package com.iroegbulam.princewill.mecash.dto.request;

import com.iroegbulam.princewill.mecash.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AccountCreationRequest(@NotNull(message = "account type cannot be blank") AccountType accountType,
                                     @NotBlank(message = "Account name cannot be blank") String accountName,
                                     @NotBlank(message = "Account currency cannot be blank") @Size(min = 3, max = 3, message = "Please enter 3 character currency code") String currencyCode,
                                     String registrationNumber,
                                     List<String> signatories) {
}
