package com.iroegbulam.princewill.mecash.exception;


public class CurrencyNotSupportedException extends RuntimeException{
    public CurrencyNotSupportedException(String message){
        super(message);
    }
}
