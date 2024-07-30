package com.iroegbulam.princewill.mecash.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class AccountBalanceResponse implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("accounts")
    private final List<AccountDto> accounts;

    @Getter
    @RequiredArgsConstructor
    public static class AccountDto implements Serializable{
        @Serial
        private static final long serialVersionUID = 1L;
        private final String accountNumber;
        private final String accountName;
        private final String accountCurrency;
        private final String accountType;
        private final double availableBalance;
    }
}
