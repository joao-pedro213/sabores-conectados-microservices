package com.postech.authorizationservice.api.config;

import com.postech.authorizationservice.service.IdentityPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OAuth2TokenCustomizerImplTest {

    private OAuth2TokenCustomizerImpl customizer;
    private JwtEncodingContext context;
    private JwtClaimsSet.Builder claimsBuilder;
    private Authentication principal;
    private UUID identityId;
    private String role;

    @BeforeEach
    void setUp() {
        this.customizer = new OAuth2TokenCustomizerImpl();
        this.context = Mockito.mock(JwtEncodingContext.class);
        this.claimsBuilder = JwtClaimsSet.builder();
        this.principal = Mockito.mock(Authentication.class);
        this.identityId = UUID.randomUUID();
        this.role = "CUSTOMER";
        IdentityPrincipal identityPrincipal = IdentityPrincipal.builder().id(this.identityId).role(this.role).build();
        when(this.context.getPrincipal()).thenReturn(this.principal);
        when(this.principal.getPrincipal()).thenReturn(identityPrincipal);
        when(this.context.getClaims()).thenReturn(this.claimsBuilder);
    }

    private static Stream<Arguments> validTokenAndGrantTypes() {
        return Stream.of(
                Arguments.of(OAuth2TokenType.ACCESS_TOKEN, AuthorizationGrantType.AUTHORIZATION_CODE),
                Arguments.of(OAuth2TokenType.ACCESS_TOKEN, AuthorizationGrantType.REFRESH_TOKEN),
                Arguments.of(OAuth2TokenType.REFRESH_TOKEN, AuthorizationGrantType.AUTHORIZATION_CODE),
                Arguments.of(OAuth2TokenType.REFRESH_TOKEN, AuthorizationGrantType.REFRESH_TOKEN)
        );
    }

    @ParameterizedTest
    @MethodSource("validTokenAndGrantTypes")
    void shouldAddUserIdClaimForValidTokenAndGrantTypes(OAuth2TokenType tokenType, AuthorizationGrantType grantType) {
        // Given
        when(this.context.getTokenType()).thenReturn(tokenType);
        when(this.context.getAuthorizationGrantType()).thenReturn(grantType);

        // When
        this.customizer.customize(this.context);

        // Then
        JwtClaimsSet claims = this.claimsBuilder.build();
        assertThat(claims.getClaim("usr").toString()).hasToString(this.identityId.toString());
        assertThat(claims.getClaim("role").toString()).hasToString(this.role);
        verify(context).getClaims();
        verify(context).getPrincipal();
    }
}
