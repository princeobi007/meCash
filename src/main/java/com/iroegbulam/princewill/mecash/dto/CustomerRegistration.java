package com.iroegbulam.princewill.mecash.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public record CustomerRegistration(@Digits(integer = 11, fraction = 0, message = "Phone number must be digits")
                                   @NotBlank(message = "phone cannot be blank")
                                   @Size(min = 11, max = 11, message = "phone number must be 11 digits") String phoneNumber,
                                   @Email(message = "Invalid email format") @NotBlank(message = "Email cannot be blank") String email,
                                   @NotBlank(message = "Password cannot be blank") @Size(min = 8, message = "Password must be at least 8 characters") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$",
                                           message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character. Length must be between 8 and 16 characters.") String password,
                                   @Past(message = "date of birth must be in the past") LocalDate dateOfBirth,
                                   @NotBlank(message = "Name cannot be blank") String firstname, String middleName,
                                   @NotBlank(message = "Name cannot be blank") String lastName,
                                   @Digits(integer = 11, fraction = 0, message = "NIN must be digits") @Size(min = 11, max = 11, message = "NIN must me 11 digits") @NotBlank(message = "NIN cannot be blank") String nin,
                                   @Digits(integer = 11, fraction = 0, message = "BVN must be digits") @Size(min = 11, max = 11, message = "BVN must me 11 digits") @NotBlank(message = "BVN cannot be blank") String bvn) {
}


