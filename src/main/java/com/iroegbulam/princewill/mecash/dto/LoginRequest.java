package com.iroegbulam.princewill.mecash.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest (
    @Schema(description = "phone", example = "08033027065")
    @NotBlank(message = "phone cannot be blank")
    @Size(min = 11, max = 11, message = "phone number must be 11 digits")
    String phone,

    @Schema(description = "password", example = "12ueURU@3456")
    @NotBlank(message = "Password cannot be blank")
    @NotBlank(message = "Password cannot be blank") @Size(min = 8, message = "Password must be at least 8 characters") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character. Length must be between 8 and 16 characters.")
    String password) {
}
