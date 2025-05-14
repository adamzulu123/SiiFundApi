package com.fund.app.box.exception;

import com.fund.app.box.model.Currency;

public class CurrencyRateNotFoundException extends RuntimeException {
    public CurrencyRateNotFoundException(Currency currency) {
        super("Rate for currency: " + currency + " not found");
    }
}
