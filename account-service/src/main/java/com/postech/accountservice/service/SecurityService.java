package com.postech.accountservice.service;

import com.postech.accountservice.data.repository.IAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("securityService")
@AllArgsConstructor
public class SecurityService {
    private final IAccountRepository repository;

    public boolean isCustomer() {
        return getJwtTokenFromSecurityContext().getClaimAsString("role").equals("CUSTOMER");
    }

    public boolean isRestaurantOwner() {
        return getJwtTokenFromSecurityContext().getClaimAsString("role").equals("RESTAURANT_OWNER");
    }

    public boolean isRestaurantManager() {
        return getJwtTokenFromSecurityContext().getClaimAsString("role").equals("RESTAURANT_MANAGER");
    }

    public boolean isResourceOwner(UUID accountId) {
        UUID identityId = UUID.fromString(getJwtTokenFromSecurityContext().getClaimAsString("usr"));
        return this.repository
                .findById(accountId)
                .filter(accountDocument -> identityId.equals(accountDocument.getIdentityId()))
                .isPresent();
    }

    private static Jwt getJwtTokenFromSecurityContext() {
        return ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();
    }
}
