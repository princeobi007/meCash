package com.iroegbulam.princewill.mecash.dto.response;

import com.iroegbulam.princewill.mecash.enums.TransactionStatus;

public record WithdrawlResponse(TransactionStatus status, double availableBalance) {
}
