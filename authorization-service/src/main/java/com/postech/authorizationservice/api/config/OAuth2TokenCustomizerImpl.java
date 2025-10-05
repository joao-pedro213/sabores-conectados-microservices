package com.postech.authorizationservice.api.config;

import com.postech.authorizationservice.service.IdentityPrincipal;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@Component
public class OAuth2TokenCustomizerImpl implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        if (this.isAccessToken(context.getTokenType())
                && this.isEitherAuthorizationCodeOrRefreshTokenGrantType(context.getAuthorizationGrantType())) {
            IdentityPrincipal identityPrincipal = (IdentityPrincipal) context.getPrincipal().getPrincipal();
            context
                    .getClaims()
                    .claim("usr", identityPrincipal.getId())
                    .claim("role", identityPrincipal.getRole());
        }
    }

    private boolean isAccessToken(OAuth2TokenType tokenType) {
        return tokenType.equals(OAuth2TokenType.ACCESS_TOKEN) || tokenType.equals(OAuth2TokenType.REFRESH_TOKEN);
    }

    private boolean isEitherAuthorizationCodeOrRefreshTokenGrantType(AuthorizationGrantType grantType) {
        return (grantType.equals(AuthorizationGrantType.AUTHORIZATION_CODE)
                || grantType.equals(AuthorizationGrantType.REFRESH_TOKEN));
    }
}
