package com.postech.orderservice.service;

import com.postech.orderservice.data.client.RestaurantClient;
import com.postech.orderservice.data.repository.IOrderRepository;
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
    private final IOrderRepository repository;
    private static final String READ_SCOPE = "SCOPE_order:read";
    private static final String WRITE_SCOPE = "SCOPE_order:write";
    private static final String API_READ_SCOPE = "SCOPE_order:api:read";

    public Mono<Boolean> canCreateOrder() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    boolean hasWriteScope = authentication
                            .getAuthorities()
                            .stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(WRITE_SCOPE));
                    return isCustomer().map(isCustomer -> (isCustomer && hasWriteScope));
                })
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> canReadRestaurant(UUID restaurantId) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    boolean hasApiReadScope = authentication
                            .getAuthorities()
                            .stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(API_READ_SCOPE));
                    if (hasApiReadScope) {
                        return Mono.just(true);
                    }
                    boolean hasReadScope = authentication
                            .getAuthorities()
                            .stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(READ_SCOPE));
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

    public Mono<Boolean> canChangeOrderStatus(UUID orderId) {
        return this.repository
                .findById(orderId)
                .flatMap(order -> {
                    UUID restaurantId = order.getRestaurantId();
                    return ReactiveSecurityContextHolder
                            .getContext()
                            .map(SecurityContext::getAuthentication)
                            .flatMap(authentication -> {
                                boolean hasWriteScope = authentication
                                        .getAuthorities()
                                        .stream()
                                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(WRITE_SCOPE));
                                if (!hasWriteScope) {
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
                            });
                })
                .defaultIfEmpty(false);
    }

    public Mono<UUID> getIdentityFromSecurityContext() {
        return getJwtTokenFromSecurityContext()
                .map(jwt -> jwt.getClaimAsString("usr"))
                .map(UUID::fromString);
    }

    private Mono<Boolean> isResourceOwner(UUID restaurantId) {
        return this.restaurantClient
                .findById(restaurantId)
                .flatMap(restaurant ->
                        Mono
                                .zip(this.getIdentityFromSecurityContext(), isRestaurantOwner(), isRestaurantManager())
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

    private static Mono<Boolean> isCustomer() {
        return getJwtTokenFromSecurityContext()
                .map(jwt -> "CUSTOMER".equals(jwt.getClaimAsString("role")))
                .defaultIfEmpty(false);
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
