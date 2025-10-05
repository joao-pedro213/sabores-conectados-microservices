package com.postech.accountservice.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClient(
            RestClient.Builder builder,
            OAuth2AuthorizedClientManager authorizedClientManager,
            @Value("${gateway.host}") String baseUrl) {
        OAuth2ClientHttpRequestInterceptor requestInterceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        requestInterceptor.setClientRegistrationIdResolver((HttpRequest httpRequest) -> "sabores-conectados");
        return builder.baseUrl(baseUrl).requestInterceptor(requestInterceptor).build();
    }
}
