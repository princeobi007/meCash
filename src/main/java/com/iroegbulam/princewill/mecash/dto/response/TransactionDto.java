package com.iroegbulam.princewill.mecash.dto.response;

import com.iroegbulam.princewill.mecash.enums.TransactionCategory;
import com.iroegbulam.princewill.mecash.enums.TransactionType;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.iroegbulam.princewill.mecash.domain.Transaction}
 */
@Value
public class TransactionDto implements Serializable {
    String transactionRef;
    double amount;
    String currencyCode;
    TransactionType transactionType;
    TransactionCategory transactionCategory;
    String narration;
    LocalDateTime createdAt;

}