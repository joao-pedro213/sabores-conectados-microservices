package com.postech.aggregatorservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(
            ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
            @Value("${gateway.host}") String baseUrl) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauthFilter.setDefaultClientRegistrationId("sabores-conectados");
        return WebClient
                .builder()
                .baseUrl(baseUrl)
                .filter(oauthFilter)
                .build();
    }
}
