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
}
