package com.postech.aggregatorservice.service;

import com.postech.aggregatorservice.data.client.RestaurantClient;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service("securityService")
@AllArgsConstructor
public class SecurityService {
    private final RestaurantClient restaurantClient;
    private static final String READ_ORDER_SCOPE = "SCOPE_order:read";
    private static final String READ_RESERVATION_SCOPE = "SCOPE_reservation:read";

    public Mono<Boolean> canReadRestaurantOrders(UUID restaurantId) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    boolean hasReadScope = authentication
                            .getAuthorities()
                            .stream()
                            .anyMatch(grantedAuthority ->
                                    grantedAuthority.getAuthority().equals(READ_ORDER_SCOPE));
                    if (!hasReadScope) {
                        return Mono.just(false);
                    }
                    return Mono
                            .zip(isRestaurantOwner(), isRestaurantManager())
                            .flatMap(tuple -> {
                                boolean isRestaurantOwner = tuple.getT1();
                                boolean isRestaurantManager = tuple.getT2();
                                if (isRestaurantOwner || isRestaurantManager) {
                                    return this.isResourceOwner(restaurantId);
                                }
                                return Mono.just(false);
                            });
                })
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> canReadRestaurantReservations(UUID restaurantId) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    boolean hasReadScope = authentication
                            .getAuthorities()
                            .stream()
                            .anyMatch(grantedAuthority ->
                                    grantedAuthority.getAuthority().equals(READ_RESERVATION_SCOPE));
                    if (!hasReadScope) {
                        return Mono.just(false);
                    }
                    return Mono
                            .zip(isRestaurantOwner(), isRestaurantManager())
                            .flatMap(tuple -> {
                                boolean isRestaurantOwner = tuple.getT1();
                                boolean isRestaurantManager = tuple.getT2();
                                if (isRestaurantOwner || isRestaurantManager) {
                                    return this.isResourceOwner(restaurantId);
                                }
                                return Mono.just(false);
                            });
                })
                .defaultIfEmpty(false);
    }

    private static Mono<UUID> getIdentityFromSecurityContext() {
        return getJwtTokenFromSecurityContext()
                .map(jwt -> jwt.getClaimAsString("usr"))
                .map(UUID::fromString);
    }

    private Mono<Boolean> isResourceOwner(UUID restaurantId) {
        return this.restaurantClient
                .findById(restaurantId)
                .flatMap(restaurant ->
                        Mono
                                .zip(getIdentityFromSecurityContext(), isRestaurantOwner(), isRestaurantManager())
                                .map(tuple -> {
                                    UUID identity = tuple.getT1();
                                    boolean isRestaurantOwner = tuple.getT2();
                                    boolean isRestaurantManager = tuple.getT3();
                                    if (isRestaurantOwner) {
                                        return restaurant.getOwnerId().equals(identity);
                                    }
                                    if (isRestaurantManager) {
                                        return restaurant.getManagerIds() != null
                                                && restaurant.getManagerIds().contains(identity);
                                    }
                                    return false;
                                })
                )
                .defaultIfEmpty(false);
    }

    private static Mono<Jwt> getJwtTokenFromSecurityContext() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken);
    }

    private static Mono<Boolean> isRestaurantOwner() {
        return getJwtTokenFromSecurityContext()
                .map(jwt -> "RESTAURANT_OWNER".equals(jwt.getClaimAsString("role")))
                .defaultIfEmpty(false);
    }

    private static Mono<Boolean> isRestaurantManager() {
        return getJwtTokenFromSecurityContext()
                .map(jwt -> "RESTAURANT_MANAGER".equals(jwt.getClaimAsString("role")))
                .defaultIfEmpty(false);
    }
}
