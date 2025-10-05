package com.postech.authorizationservice.service;

import com.postech.authorizationservice.data.document.ClientDocument;
import com.postech.authorizationservice.data.repository.IClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RegisteredClientRepositoryImpl implements RegisteredClientRepository {
    private final IClientRepository repository;

    @Override
    public void save(RegisteredClient registeredClient) {
    }

    @Override
    public RegisteredClient findById(String id) {
        return this.toRegisteredClient(this.repository.findById(UUID.fromString(id)).orElse(null));
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return this.toRegisteredClient(this.repository.findByClientId(clientId).orElse(null));
    }

    private RegisteredClient toRegisteredClient(ClientDocument clientDocument) {
        if (clientDocument == null) {
            return null;
        }
        RegisteredClient.Builder builder = RegisteredClient
                .withId(clientDocument.getId().toString())
                .clientId(clientDocument.getClientId());
        if (clientDocument.isPublicClient()) {
            builder
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .clientSettings(ClientSettings.builder().requireProofKey(true).build());
        } else {
            builder
                    .clientSecret(clientDocument.getClientSecret())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
        }
        builder
                .redirectUris(uris -> uris.addAll(clientDocument.getRedirectUris()))
                .scopes(scopes -> scopes.addAll(clientDocument.getScopes()))
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(6)).build());
        return builder.build();
    }
}
