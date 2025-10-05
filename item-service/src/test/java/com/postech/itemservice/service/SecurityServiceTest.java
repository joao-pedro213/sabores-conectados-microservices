package com.postech.itemservice.service;

import com.postech.itemservice.dto.RestaurantResponseDto;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        ReflectionTestUtils.setField(securityService, "restaurantRoute", "/api/restaurant");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @CsvSource({
            "RESTAURANT_OWNER, true, false",
            "RESTAURANT_MANAGER, false, true",
            "CUSTOMER, false, false"
    })
    void testRoleChecks(String role, boolean isRestaurantOwner, boolean isRestaurantManager) {
        // Given
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("role", role)
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When & Then
        assertThat(isRestaurantOwner).isEqualTo(this.securityService.isRestaurantOwner());
        assertThat(isRestaurantManager).isEqualTo(this.securityService.isRestaurantManager());
    }

    @Test
    void isResourceOwnerShouldReturnTrueWhenUserIsOwner() {
        // Given
        when(this.restClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(anyString(), any(UUID.class))).thenReturn(this.requestHeadersSpec);
        when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
        UUID ownerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        RestaurantResponseDto restaurantResponseDto = RestaurantResponseDto
                .builder()
                .id(restaurantId)
                .ownerId(ownerId)
                .build();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("usr", ownerId.toString())
                .claim("role", "RESTAURANT_OWNER")
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(this.responseSpec.body(RestaurantResponseDto.class)).thenReturn(restaurantResponseDto);

        // When & Then
        assertTrue(this.securityService.isResourceOwner(restaurantId));
    }

    @Test
    void isResourceOwnerShouldReturnFalseWhenUserIsNotOwner() {
        // Given
        when(this.restClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(anyString(), any(UUID.class))).thenReturn(this.requestHeadersSpec);
        when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
        UUID ownerId = UUID.randomUUID();
        UUID anotherIdentityId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        RestaurantResponseDto restaurantResponseDto = RestaurantResponseDto
                .builder()
                .id(restaurantId)
                .ownerId(ownerId)
                .build();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("usr", anotherIdentityId.toString())
                .claim("role", "RESTAURANT_OWNER")
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(this.responseSpec.body(RestaurantResponseDto.class)).thenReturn(restaurantResponseDto);

        // When & Then
        assertFalse(this.securityService.isResourceOwner(restaurantId));
    }

    @Test
    void isResourceOwnerShouldReturnFalseWhenRestaurantNotFound() {
        // Given
        when(this.restClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(anyString(), any(UUID.class))).thenReturn(this.requestHeadersSpec);
        when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
        UUID restaurantId = UUID.randomUUID();
        UUID identityId = UUID.randomUUID();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("usr", identityId.toString())
                .claim("role", "RESTAURANT_OWNER")
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(this.responseSpec.body(RestaurantResponseDto.class)).thenReturn(null);

        // When & Then
        assertFalse(this.securityService.isResourceOwner(restaurantId));
    }
}
