package com.fund.app.box.exception;

import com.fund.app.box.model.Currency;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(Currency currency) {
        super("Rate for currency: " + currency + " not found");
    }
}
