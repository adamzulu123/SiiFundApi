package com.fund.app.box.util;


import com.fund.app.box.model.Currency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

//todo: for now its done with static final exchange rates for 3 available currencies,
// but maybe I will be able to finish it with online API current exchange rates
@Component
public class CurrencyConverter {
    private static final Map<Currency, Map<Currency, BigDecimal>> exchangeRates = Map.of(
            Currency.EUR, Map.of(
                    Currency.EUR, BigDecimal.ONE,
                    Currency.USD, new BigDecimal("1.10"),
                    Currency.GBP, new BigDecimal("0.85")
            ),
            Currency.USD, Map.of(
                    Currency.EUR, new BigDecimal("0.91"),
                    Currency.USD, BigDecimal.ONE,
                    Currency.GBP, new BigDecimal("0.77")
            ),
            Currency.GBP, Map.of(
                    Currency.EUR, new BigDecimal("1.18"),
                    Currency.USD, new BigDecimal("1.30"),
                    Currency.GBP, BigDecimal.ONE
            )
    );

    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from == null || to == null || amount == null) throw new IllegalArgumentException("Currency or amount is null");
        BigDecimal rate = exchangeRates.get(from).get(to);
        return amount.multiply(rate);
    }




}
