package com.postech.orderservice.service;

import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.orderservice.data.client.RestaurantClient;
import com.postech.orderservice.data.document.OrderDocument;
import com.postech.orderservice.data.repository.IOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @InjectMocks
    private SecurityService securityService;

    @Mock
    private RestaurantClient restaurantClient;

    @Mock
    private IOrderRepository orderRepository;

    @Test
    void canCreateOrderShouldReturnTrueWhenUserIsCustomerAndHasWriteScope() {
        // Given
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "CUSTOMER")
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_order:write")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        Mono<Boolean> result = this.securityService
                .canCreateOrder()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
    }

    @Test
    void canCreateOrderShouldReturnFalseWhenUserIsCustomerAndDoesNotHaveWriteScope() {
        // Given
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "CUSTOMER")
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt, Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        Mono<Boolean> result = this.securityService
                .canCreateOrder()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(false).verifyComplete();
    }

    @Test
    void canCreateOrderShouldReturnFalseWhenUserIsNotCustomer() {
        // Given
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "RESTAURANT_OWNER")
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_order:write")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        Mono<Boolean> result = this.securityService
                .canCreateOrder()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(false).verifyComplete();
    }

    @Test
    void canCreateOrderShouldReturnFalseWhenNoSecurityContext() {
        // When
        Mono<Boolean> result = this.securityService.canCreateOrder();

        // Then
        StepVerifier.create(result).expectNext(false).verifyComplete();
    }

    @Test
    void canReadRestaurantShouldReturnTrueWhenUserHasApiReadScope() {
        // Given
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .subject("other-microservice")
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_order:api:read")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        Mono<Boolean> result = this.securityService
                .canReadRestaurant(UUID.randomUUID())
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
    }

    @Test
    void canReadRestaurantShouldReturnTrueWhenUserIsRestaurantOwnerAndHasReadScope() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "RESTAURANT_OWNER")
                .claim("usr", userId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_order:read")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        RestaurantDto restaurantDto = RestaurantDto.builder().id(restaurantId).ownerId(userId).build();
        when(this.restaurantClient.findById(any(UUID.class))).thenReturn(Mono.just(restaurantDto));

        // When
        Mono<Boolean> result = this.securityService
                .canReadRestaurant(restaurantId)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
    }

    @Test
    void canReadRestaurantShouldReturnTrueWhenUserIsRestaurantManagerAndHasReadScope() {
        // Given
        UUID managerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "RESTAURANT_MANAGER")
                .claim("usr", managerId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_order:read")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        RestaurantDto restaurantDto = RestaurantDto.builder().id(restaurantId).managerIds(List.of(managerId)).build();
        when(this.restaurantClient.findById(any(UUID.class))).thenReturn(Mono.just(restaurantDto));

        // When
        Mono<Boolean> result = this.securityService
                .canReadRestaurant(restaurantId)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
    }

    @Test
    void canChangeOrderStatusShouldReturnTrueWhenUserIsRestaurantOwnerAndHasWriteScope() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "RESTAURANT_OWNER")
                .claim("usr", userId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_order:write")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        OrderDocument orderDocument = OrderDocument.builder().restaurantId(restaurantId).build();
        when(this.orderRepository.findById(any(UUID.class))).thenReturn(Mono.just(orderDocument));
        RestaurantDto restaurantDto = RestaurantDto.builder().id(restaurantId).ownerId(userId).build();
        when(this.restaurantClient.findById(any(UUID.class))).thenReturn(Mono.just(restaurantDto));

        // When
        Mono<Boolean> result = this.securityService
                .canChangeOrderStatus(orderId)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
    }

    @Test
    void canChangeOrderStatusShouldReturnTrueWhenUserIsRestaurantManagerAndHasWriteScope() {
        // Given
        UUID managerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "RESTAURANT_MANAGER")
                .claim("usr", managerId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_order:write")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        OrderDocument orderDocument = OrderDocument.builder().restaurantId(restaurantId).build();
        when(this.orderRepository.findById(any(UUID.class))).thenReturn(Mono.just(orderDocument));
        RestaurantDto restaurantDto = RestaurantDto.builder().id(restaurantId).managerIds(List.of(managerId)).build();
        when(this.restaurantClient.findById(any(UUID.class))).thenReturn(Mono.just(restaurantDto));

        // When
        Mono<Boolean> result = this.securityService
                .canChangeOrderStatus(orderId)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
    }

    @Test
    void getIdentityFromSecurityContextShouldReturnUserId() {
        // Given
        UUID userId = UUID.randomUUID();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("usr", userId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        Mono<UUID> result = this.securityService
                .getIdentityFromSecurityContext()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(userId).verifyComplete();
    }
}
