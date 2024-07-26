package com.iroegbulam.princewill.mecash.domain;

import java.time.LocalDateTime;

public record LoginAttempt(String phone,
                           boolean success,
                           LocalDateTime createdAt) {

}