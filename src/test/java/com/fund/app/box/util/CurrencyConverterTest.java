package com.fund.app.box.util;

import com.fund.app.box.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;


public class CurrencyConverterTest {

    private final CurrencyConverter converter = new CurrencyConverter();

    @Test
    void testConvert() {
        BigDecimal results1 = converter.convert(new BigDecimal(10), Currency.EUR, Currency.EUR);
        BigDecimal result2 = converter.convert(new BigDecimal("100"), Currency.EUR, Currency.USD);
        BigDecimal result3 = converter.convert(new BigDecimal("100"), Currency.USD, Currency.GBP);

        assertEquals(new BigDecimal("77.00"), result3);
        assertEquals(new BigDecimal("110.00"), result2);
        assertEquals(new BigDecimal(10), results1);
    }

    @Test
    void testConvertNull() {
        assertThrows(IllegalArgumentException.class,
                () -> converter.convert(null, Currency.EUR, Currency.EUR));
    }
}
