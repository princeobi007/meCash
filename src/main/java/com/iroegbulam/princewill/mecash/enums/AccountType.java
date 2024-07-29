package com.iroegbulam.princewill.mecash.enums;

import lombok.Getter;

@Getter
public enum AccountType {

    I("Individual"),
    LLC("LLC"),
    BN("Business Name");

    private final String name;

    AccountType(String name){
        this.name = name;
    }
}
