package com.fund.app.box.exception;

public class ApiCurrencyRatesUnavailableException extends RuntimeException {
    public ApiCurrencyRatesUnavailableException(String message) {
        super(message);
    }
}
