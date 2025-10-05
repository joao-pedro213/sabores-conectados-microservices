package com.postech.restaurantservice.service;

import com.postech.restaurantservice.data.repository.IRestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("securityService")
@AllArgsConstructor
public class SecurityService {
    private final IRestaurantRepository repository;

    public boolean isCustomer() {
        return getJwtTokenFromSecurityContext().getClaimAsString("role").equals("CUSTOMER");
    }

    public boolean isRestaurantOwner() {
        return getJwtTokenFromSecurityContext().getClaimAsString("role").equals("RESTAURANT_OWNER");
    }

    public boolean isRestaurantManager() {
        return getJwtTokenFromSecurityContext().getClaimAsString("role").equals("RESTAURANT_MANAGER");
    }

    public boolean isResourceOwner(UUID restaurantId) {
        return this.repository
                .findById(restaurantId)
                .filter(document -> getIdentityFromSecurityContext().equals(document.getOwnerId()))
                .isPresent();
    }

    public UUID getIdentityFromSecurityContext() {
        return UUID.fromString(getJwtTokenFromSecurityContext().getClaimAsString("usr"));
    }

    private static Jwt getJwtTokenFromSecurityContext() {
        return ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();
    }
}
