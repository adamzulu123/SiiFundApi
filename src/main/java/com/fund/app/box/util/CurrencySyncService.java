package com.fund.app.box.util;

import com.fund.app.box.dto.ExchangeRateDto;
import com.fund.app.box.dto.Rate;
import com.fund.app.box.exception.ApiCurrencyRatesUnavailableException;
import com.fund.app.box.exception.CurrencyRateNotFoundException;
import com.fund.app.box.model.Currency;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CurrencySyncService is responsible for fetching and storing exchange rates for selected currencies.
 *
 * It connects to the official NBP API at application startup and retrieves the current exchange rates for
 * USD, EUR, GBP, and sets PLN as a static base (with rate = 1). These rates are stored in-memory
 * in a private Map and are accessed via {@link #getMid(Currency)}.
 * We store rates in-memory for simplicity, I believe it's better for this task.
 *
 * If fetching data from the API fails, a fallback method provides hardcoded exchange rates,
 * allowing the application to remain functional and testable.
 *
 * In a production-ready application, this class would have more functionalities like:
 * periodic updates with @Scheduled, error handling with retires and saving rates to the database (probably).
 */
@Slf4j
@Service
public class CurrencySyncService {
//    private final WebClient webClient = WebClient.builder()
//            .baseUrl("https://api.nbp.pl/api")
//            .build();
    private final WebClient webClient;

    private static final List<String> AVAILABLE_CURRENCIES = List.of("USD", "EUR", "GBP", "PLN");

    private Map<Currency, BigDecimal> rates = Map.of(); //we save rates in CurrencySyncService bean memory

    public CurrencySyncService(@Qualifier("nbpWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public BigDecimal getMid(Currency currency) {
        BigDecimal rate = rates.get(currency); //IllegalArgumentException if currency dont exists
        if (rate == null) throw new CurrencyRateNotFoundException(currency);
        return rate;
    }

    @PostConstruct
    public void init() {
        try{
            Map<Currency, BigDecimal> fetchedRates = fetchSelectedCurrenciesRates().block(); //in this case blocking but usually subscribe is more suitable
            if (fetchedRates == null || fetchedRates.isEmpty()) {
                throw new ApiCurrencyRatesUnavailableException("Couldn't fetch currencies rates");
            }
            rates = fetchedRates;
            log.info("Fetched currencies rates from nbp api " + fetchedRates);
        }catch (Exception e){
            log.warn("Error while fetching rates from nbp api: {} — using basic static rates", e.getMessage());
            rates = fallbackRates();
        }
    }

    public Mono<Map<Currency, BigDecimal>> fetchSelectedCurrenciesRates(){
        return webClient.get()
                .uri("/exchangerates/tables/A")
                .retrieve()
                .bodyToMono(ExchangeRateDto[].class)
                .map(response ->{
                    if(response.length == 0) return Map.<Currency, BigDecimal>of(); //return empty map
                    ExchangeRateDto exchangeRateDto = response[0]; //if returns something get response
                    Map<Currency, BigDecimal> map = exchangeRateDto.getRates().stream()
                            .filter(r -> AVAILABLE_CURRENCIES.contains(r.getCode()))
                            .collect(Collectors.toMap(
                                    r -> Currency.valueOf(r.getCode()),
                                    Rate::getMid
                            ));

                    map.put(Currency.PLN, BigDecimal.ONE);
                    return Collections.unmodifiableMap(map);
                });
    }

    //generally in real application this shouldn't be done but in for this task it's good, because if there is some
    //error with nbp-api we still have chance to test out application.
    private Map<Currency, BigDecimal> fallbackRates() {
        return Map.of(
                Currency.USD, new BigDecimal("3.75"),
                Currency.EUR, new BigDecimal("4.25"),
                Currency.GBP, new BigDecimal("5.05"),
                Currency.PLN, BigDecimal.ONE
        );
    }


}
