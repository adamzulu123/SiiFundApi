package com.fund.app.box.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient nbpWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.nbp.pl/api")
                .build();
    }
}