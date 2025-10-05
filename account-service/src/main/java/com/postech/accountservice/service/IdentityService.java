package com.postech.accountservice.service;

import com.postech.accountservice.dto.NewIdentityRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class IdentityService {
    @Value("${gateway.identity.route}")
    private String identityRoute;
    private final RestClient restClient;

    public IdentityService(RestClient restClient) {
        this.restClient = restClient;
    }

    public UUID create(NewIdentityRequestDto requestDto) {
        return this.restClient
                .post()
                .uri(identityRoute + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto)
                .retrieve()
                .body(UUID.class);
    }
}
