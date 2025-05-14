package com.fund.app.box.util;

import com.fund.app.box.dto.ExchangeRateDto;
import com.fund.app.box.dto.Rate;
import com.fund.app.box.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencySyncServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private CurrencySyncService currencySyncService;

    @BeforeEach
    void setUp() {
        currencySyncService = new CurrencySyncService(webClient);

        // Setup WebClient mock chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void fetchSelectedCurrenciesRates_success() {
        // Given
        ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
        List<Rate> rates = List.of(
                new Rate("USD", "USD", new BigDecimal("3.75")),
                new Rate("EUR", "EUR", new BigDecimal("4.25")),
                new Rate("GBP", "GBP", new BigDecimal("5.05")),
                new Rate("CHF", "CHF", new BigDecimal("4.10")) // Not in AVAILABLE_CURRENCIES, should be filtered out
        );
        exchangeRateDto.setRates(rates);

        ExchangeRateDto[] response = new ExchangeRateDto[]{exchangeRateDto};

        when(responseSpec.bodyToMono(ExchangeRateDto[].class)).thenReturn(Mono.just(response));

        Map<Currency, BigDecimal> result = currencySyncService.fetchSelectedCurrenciesRates().block();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(new BigDecimal("3.75"), result.get(Currency.USD));
        assertEquals(new BigDecimal("4.25"), result.get(Currency.EUR));
        assertEquals(new BigDecimal("5.05"), result.get(Currency.GBP));
        assertEquals(BigDecimal.ONE, result.get(Currency.PLN));
        assertFalse(result.containsKey("CHF")); // Should be filtered out
    }

    @Test
    void init_fallbackWhenApiFails() {
        when(responseSpec.bodyToMono(ExchangeRateDto[].class)).thenReturn(Mono.error(new RuntimeException("API unavailable")));

        currencySyncService.init();

        assertEquals(new BigDecimal("3.75"), currencySyncService.getMid(Currency.USD));
        assertEquals(new BigDecimal("4.25"), currencySyncService.getMid(Currency.EUR));
        assertEquals(new BigDecimal("5.05"), currencySyncService.getMid(Currency.GBP));
        assertEquals(BigDecimal.ONE, currencySyncService.getMid(Currency.PLN));
    }

    @Test
    void init_fallbackWhenEmptyResponse() {
        ExchangeRateDto[] emptyResponse = new ExchangeRateDto[0];
        when(responseSpec.bodyToMono(ExchangeRateDto[].class)).thenReturn(Mono.just(emptyResponse));

        currencySyncService.init();

        assertEquals(new BigDecimal("3.75"), currencySyncService.getMid(Currency.USD));
        assertEquals(new BigDecimal("4.25"), currencySyncService.getMid(Currency.EUR));
        assertEquals(new BigDecimal("5.05"), currencySyncService.getMid(Currency.GBP));
        assertEquals(BigDecimal.ONE, currencySyncService.getMid(Currency.PLN));
    }

    @Test
    void testGetMid_CurrencyNotPresentInRates_ThrowsCurrencyNotFoundException() {
        ExchangeRateDto[] emptyResponse = new ExchangeRateDto[0];
        when(responseSpec.bodyToMono(ExchangeRateDto[].class)).thenReturn(Mono.just(emptyResponse));
        currencySyncService.init();

        assertThrows(IllegalArgumentException.class, () -> currencySyncService.getMid(Currency.valueOf("CHF")));
    }
}
