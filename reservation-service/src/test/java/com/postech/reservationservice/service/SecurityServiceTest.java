package com.postech.reservationservice.service;

import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.reservationservice.data.client.RestaurantClient;
import com.postech.reservationservice.data.document.ReservationDocument;
import com.postech.reservationservice.data.repository.IReservationRepository;
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
    private IReservationRepository reservationRepository;

    @Test
    void canCreateReservationShouldReturnTrueWhenUserIsCustomerAndHasWriteScope() {
        // Given
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "CUSTOMER")
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_reservation:write")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        Mono<Boolean> result = this.securityService
                .canCreateReservation()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
    }

    @Test
    void canCreateReservationShouldReturnFalseWhenUserIsCustomerAndDoesNotHaveWriteScope() {
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
                .canCreateReservation()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(false).verifyComplete();
    }

    @Test
    void canCreateReservationShouldReturnFalseWhenUserIsNotCustomer() {
        // Given
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "RESTAURANT_OWNER")
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_reservation:write")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        Mono<Boolean> result = this.securityService
                .canCreateReservation()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(false).verifyComplete();
    }

    @Test
    void canCreateReservationShouldReturnFalseWhenNoSecurityContext() {
        // When
        Mono<Boolean> result = this.securityService.canCreateReservation();

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
                List.of(new SimpleGrantedAuthority("SCOPE_reservation:api:read")));
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
                List.of(new SimpleGrantedAuthority("SCOPE_reservation:read")));
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
                List.of(new SimpleGrantedAuthority("SCOPE_reservation:read")));
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
    void canChangeReservationStatusShouldReturnTrueWhenUserIsRestaurantOwnerAndHasWriteScope() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "RESTAURANT_OWNER")
                .claim("usr", userId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_reservation:write")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        ReservationDocument reservationDocument = ReservationDocument.builder().restaurantId(restaurantId).build();
        when(this.reservationRepository.findById(any(UUID.class))).thenReturn(Mono.just(reservationDocument));
        RestaurantDto restaurantDto = RestaurantDto.builder().id(restaurantId).ownerId(userId).build();
        when(this.restaurantClient.findById(any(UUID.class))).thenReturn(Mono.just(restaurantDto));

        // When
        Mono<Boolean> result = this.securityService
                .canChangeReservationStatus(reservationId)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
    }

    @Test
    void canChangeReservationStatusShouldReturnTrueWhenUserIsRestaurantManagerAndHasWriteScope() {
        // Given
        UUID managerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", "RESTAURANT_MANAGER")
                .claim("usr", managerId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_reservation:write")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        ReservationDocument reservationDocument = ReservationDocument.builder().restaurantId(restaurantId).build();
        when(this.reservationRepository.findById(any(UUID.class))).thenReturn(Mono.just(reservationDocument));
        RestaurantDto restaurantDto = RestaurantDto.builder().id(restaurantId).managerIds(List.of(managerId)).build();
        when(this.restaurantClient.findById(any(UUID.class))).thenReturn(Mono.just(restaurantDto));

        // When
        Mono<Boolean> result = this.securityService
                .canChangeReservationStatus(reservationId)
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
