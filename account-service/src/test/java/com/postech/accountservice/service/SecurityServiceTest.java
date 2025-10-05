package com.postech.accountservice.service;

import com.postech.accountservice.data.document.AccountDocument;
import com.postech.accountservice.data.repository.IAccountRepository;
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
    private IAccountRepository repository;

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
    void isResourceOwnerShouldReturnTrueWhenIdentityIsResourceOwner() {
        // Given
        UUID identityId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        AccountDocument account = AccountDocument.builder().id(accountId).identityId(identityId).build();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("usr", identityId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(this.repository.findById(accountId)).thenReturn(Optional.of(account));

        // When & Then
        assertTrue(this.securityService.isResourceOwner(accountId));
    }

    @Test
    void isResourceOwnerShouldReturnFalseWhenIdentityIsNotResourceOwner() {
        // Given
        UUID identityId = UUID.randomUUID();
        UUID anotherIdentityId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        AccountDocument account = AccountDocument.builder().id(accountId).identityId(identityId).build();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("usr", anotherIdentityId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(this.repository.findById(accountId)).thenReturn(Optional.of(account));

        // When & Then
        assertFalse(this.securityService.isResourceOwner(accountId));
    }

    @Test
    void isResourceOwnerShouldReturnFalseWhenAccountNotFound() {
        // Given
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Jwt jwt = Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("usr", userId.toString())
                .build();
        Authentication authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(this.repository.findById(accountId)).thenReturn(Optional.empty());

        // When & Then
        assertFalse(this.securityService.isResourceOwner(accountId));
    }
}
