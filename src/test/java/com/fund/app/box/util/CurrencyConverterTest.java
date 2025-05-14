package com.fund.app.box.util;

import com.fund.app.box.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;


public class CurrencyConverterTest {

    private CurrencyConverter converter;

    @BeforeEach
    void setUp() {
        CurrencySyncService currencySyncService = mock(CurrencySyncService.class);

        when(currencySyncService.getMid(Currency.EUR)).thenReturn(new BigDecimal("4.25"));
        when(currencySyncService.getMid(Currency.USD)).thenReturn(new BigDecimal("3.75"));
        when(currencySyncService.getMid(Currency.GBP)).thenReturn(new BigDecimal("5.05"));
        when(currencySyncService.getMid(Currency.PLN)).thenReturn(BigDecimal.ONE);

        converter = new CurrencyConverter(currencySyncService);
    }

    @Test
    void testConvert() {
        BigDecimal results1 = converter.convert(new BigDecimal("10"), Currency.EUR, Currency.EUR);
        BigDecimal result2 = converter.convert(new BigDecimal("100"), Currency.EUR, Currency.USD);
        BigDecimal result3 = converter.convert(new BigDecimal("100"), Currency.USD, Currency.GBP);

        assertEquals(new BigDecimal("74.26"), result3); //100 * 3.75 / 5.05 = 74.26
        assertEquals(new BigDecimal("113.33"), result2);
        assertEquals(new BigDecimal("10.00"), results1);
    }

    @Test
    void testConvertNull() {
        assertThrows(IllegalArgumentException.class,
                () -> converter.convert(null, Currency.EUR, Currency.EUR));
    }

    @Test
    void testConvertWithNullCurrency() {
        assertThrows(IllegalArgumentException.class, () ->
                converter.convert(new BigDecimal("100"), null, Currency.USD));

        assertThrows(IllegalArgumentException.class, () ->
                converter.convert(new BigDecimal("100"), Currency.EUR, null));
    }

    @Test
    void testConvertWithDifferentRoundingScenarios() {
        BigDecimal result1 = converter.convert(new BigDecimal("100.123"), Currency.EUR, Currency.PLN);
        BigDecimal result2 = converter.convert(new BigDecimal("100.126"), Currency.EUR, Currency.PLN);

        assertEquals(new BigDecimal("425.52"), result1); // 100.123 * 4.25 = 425.52275 →  425.52
        assertEquals(new BigDecimal("425.54"), result2); // 100.126 * 4.25 = 425.5355 → 425.54
    }

    @Test
    void testConvertWithVerySmallAmounts() {
        BigDecimal result = converter.convert(new BigDecimal("0.01"), Currency.USD, Currency.EUR);
        assertEquals(new BigDecimal("0.01"), result);
    }


}
