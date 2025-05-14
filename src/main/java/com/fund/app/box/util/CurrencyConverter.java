package com.fund.app.box.util;


import com.fund.app.box.model.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * CurrencyConverter provides conversion between supported currencies using exchange rates relative to PLN.
 * In this implementation, exchange rates are loaded once at application startup via {@link CurrencySyncService}.
 * These rates are then used for all currency conversions during the application's lifecycle.
 *
 * Note: In a real-world production system, exchange rates should be refreshed periodically (e.g., with a scheduler)
 * and ideally persisted in a database for traceability and resilience.
 *
 * For the purpose of this task, loading the rates at startup is a simple and efficient approach.
 * App won't be working longer than rates update in our api (I believe so).
 * If fetching from the external API (nbp api) fails, the system falls back to predefined static values to ensure continued functionality.
 * This might be crucial in our case, when it's more important to use and test app functionalities rather than real currency's rates.
 */

@Component
@RequiredArgsConstructor
public class CurrencyConverter {
    //previously created map is not necessary we can do it in more useful way, but of course it was cool for testing and building app

    private final CurrencySyncService currencySyncService;

    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from == null || to == null || amount == null) throw new IllegalArgumentException("Currency or amount is null");

        //getting rates with PLN to transfer between currencies
        BigDecimal fromMid = currencySyncService.getMid(from);
        BigDecimal toMid = currencySyncService.getMid(to);

        //if (fromMid == null && toMid == null) throw new IllegalArgumentException("From or To rate cannot be null");

        return amount.multiply(fromMid).divide(toMid, 2, RoundingMode.HALF_UP);
    }




}
