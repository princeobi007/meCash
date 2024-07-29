package com.iroegbulam.princewill.mecash.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(@Schema(description = "phone")
                             String phone,
                            @Schema(description = "JWT token")
                             String token) {
}
