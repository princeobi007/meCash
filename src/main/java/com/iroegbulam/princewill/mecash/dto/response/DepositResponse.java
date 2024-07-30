package com.iroegbulam.princewill.mecash.dto.response;

import com.iroegbulam.princewill.mecash.enums.TransactionStatus;

public record DepositResponse(TransactionStatus status, double currentBalance) {
}
