package com.iroegbulam.princewill.mecash.domain;

import java.time.LocalDate;

public record Customer(String firstname,
                       String middleName,
                       String lastname,
                       String customerId,
                       String phoneNumber,
                       LocalDate dob,
                       String email,
                       String bvn,
                       String nin,
                       String password) {

}
