package com.postech.itemservice.service;

import com.postech.itemservice.dto.RestaurantResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.UUID;

@Service("securityService")
public class SecurityService {
    @Value("${gateway.restaurant.route}")
    private String restaurantRoute;
    private final RestClient restClient;

    public SecurityService(RestClient restClient) {
        this.restClient = restClient;
    }

    public boolean isRestaurantOwner() {
        return getJwtTokenFromSecurityContext().getClaimAsString("role").equals("RESTAURANT_OWNER");
    }

    public boolean isRestaurantManager() {
        return getJwtTokenFromSecurityContext().getClaimAsString("role").equals("RESTAURANT_MANAGER");
    }

    public boolean isResourceOwner(UUID restaurantId) {
        return this.retrieveRestaurantById(restaurantId)
                .map(RestaurantResponseDto::getOwnerId)
                .filter(UUID.fromString(getJwtTokenFromSecurityContext().getClaimAsString("usr"))::equals)
                .isPresent();
    }

    private Optional<RestaurantResponseDto> retrieveRestaurantById(UUID restaurantId) {
        try {
            if (restaurantId == null) {
                return Optional.empty();
            }
            RestaurantResponseDto restaurant = this.restClient
                    .get()
                    .uri(this.restaurantRoute + "/{id}", restaurantId)
                    .retrieve()
                    .body(RestaurantResponseDto.class);
            return Optional.ofNullable(restaurant);
        } catch (HttpClientErrorException exception) {
            return Optional.empty();
        }
    }

    private static Jwt getJwtTokenFromSecurityContext() {
        return ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();
    }
}
