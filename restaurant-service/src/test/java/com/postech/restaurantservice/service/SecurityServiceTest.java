package com.postech.restaurantservice.service;

import com.postech.restaurantservice.data.document.RestaurantDocument;
import com.postech.restaurantservice.data.repository.IRestaurantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private IRestaurantRepository repository;

    @InjectMocks
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @CsvSource({
            "CUSTOMER, true, false, false",
            "RESTAURANT_OWNER, false, true, false",
            "RESTAURANT_MANAGER, false, false, true",
            "ADMIN, false, false, false"
    })
    void testRoleChecks(String role, boolean isCustomer, boolean isRestaurantOwner, boolean isRestaurantManager) {
        // Given
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", role)
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When & Then
        assertThat(isCustomer).isEqualTo(this.securityService.isCustomer());
        assertThat(isRestaurantOwner).isEqualTo(this.securityService.isRestaurantOwner());
        assertThat(isRestaurantManager).isEqualTo(this.securityService.isRestaurantManager());
    }

    @Test
    void isResourceOwnerShouldReturnTrueWhenUserIsOwner() {
        // Given
        UUID ownerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        RestaurantDocument restaurant = RestaurantDocument.builder().id(restaurantId).ownerId(ownerId).build();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("usr", ownerId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(this.repository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // When & Then
        assertTrue(this.securityService.isResourceOwner(restaurantId));
    }

    @Test
    void isResourceOwnerShouldReturnFalseWhenUserIsNotOwner() {
        // Given
        UUID ownerId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        RestaurantDocument restaurant = RestaurantDocument.builder().id(restaurantId).ownerId(ownerId).build();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("usr", anotherUserId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(this.repository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // When & Then
        assertFalse(this.securityService.isResourceOwner(restaurantId));
    }

    @Test
    void isResourceOwnerShouldReturnFalseWhenRestaurantNotFound() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("usr", userId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(this.repository.findById(restaurantId)).thenReturn(Optional.empty());

        // When & Then
        assertFalse(this.securityService.isResourceOwner(restaurantId));
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
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When & Then
        assertThat(this.securityService.getIdentityFromSecurityContext()).isEqualTo(userId);
    }
}
