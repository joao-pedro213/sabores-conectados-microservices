package com.postech.authorizationservice.service;

import com.postech.authorizationservice.data.document.ClientDocument;
import com.postech.authorizationservice.data.repository.IClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RegisteredClientRepositoryImplTest {

    @Mock
    private IClientRepository clientRepository;

    @InjectMocks
    private RegisteredClientRepositoryImpl registeredClientRepository;

    private ClientDocument clientDocument;

    @BeforeEach
    void setUp() {
        this.clientDocument = ClientDocument
                .builder()
                .id(UUID.randomUUID())
                .clientId("test-client")
                .clientSecret("test-secret")
                .scopes(Set.of("read", "write"))
                .redirectUris(Set.of("http://host:port/login/callback"))
                .build();
    }

    @Test
    void findByIdShouldReturnRegisteredClientWhenClientExists() {
        // Given
        when(this.clientRepository.findById(any(UUID.class))).thenReturn(Optional.of(this.clientDocument));

        // When
        RegisteredClient result = this.registeredClientRepository.findById(this.clientDocument.getId().toString());

        // Then
        assertThat(result).isNotNull();
        assertRegisteredClient(result, this.clientDocument);
    }

    @Test
    void findByIdShouldReturnNullWhenClientDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(this.clientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        RegisteredClient result = this.registeredClientRepository.findById(nonExistentId.toString());

        // Then
        assertThat(result).isNull();
    }

    @Test
    void findByClientIdShouldReturnRegisteredClientWhenClientExists() {
        // Given
        when(this.clientRepository.findByClientId("test-client")).thenReturn(Optional.of(this.clientDocument));

        // When
        RegisteredClient result = this.registeredClientRepository.findByClientId("test-client");

        // Then
        assertThat(result).isNotNull();
        assertRegisteredClient(result, this.clientDocument);
    }

    @Test
    void findByClientIdShouldReturnNullWhenClientDoesNotExist() {
        // Given
        when(this.clientRepository.findByClientId("non-existent-client")).thenReturn(Optional.empty());

        // When
        RegisteredClient result = this.registeredClientRepository.findByClientId("non-existent-client");

        // Then
        assertThat(result).isNull();
    }

    @Test
    void saveShouldDoNothingAsItIsAReadOnlyRepository() {
        assertDoesNotThrow(() -> this.registeredClientRepository.save(null));
        verifyNoInteractions(this.clientRepository);
    }

    private void assertRegisteredClient(RegisteredClient registeredClient, ClientDocument clientDocument) {
        assertThat(registeredClient.getId()).isEqualTo(clientDocument.getId().toString());
        assertThat(registeredClient.getClientId()).isEqualTo(clientDocument.getClientId());
        if (clientDocument.isPublicClient()) {
            assertThat(registeredClient.getClientAuthenticationMethods()).contains(ClientAuthenticationMethod.NONE);
            assertThat(registeredClient.getAuthorizationGrantTypes()).contains(AuthorizationGrantType.AUTHORIZATION_CODE);
            assertThat(registeredClient.getClientSettings().isRequireProofKey()).isTrue();
        } else {
            assertThat(registeredClient.getClientSecret()).isEqualTo(clientDocument.getClientSecret());
            assertThat(registeredClient.getClientAuthenticationMethods())
                    .contains(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
            assertThat(registeredClient.getAuthorizationGrantTypes())
                    .contains(AuthorizationGrantType.CLIENT_CREDENTIALS, AuthorizationGrantType.REFRESH_TOKEN);
        }
        assertThat(registeredClient.getScopes()).containsAll(clientDocument.getScopes());
        assertThat(registeredClient.getRedirectUris()).contains("http://host:port/login/callback");
        assertThat(registeredClient.getTokenSettings().getAccessTokenTimeToLive()).isEqualTo(Duration.ofHours(6));
    }
}
