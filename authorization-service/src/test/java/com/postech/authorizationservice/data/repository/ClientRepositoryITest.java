package com.postech.authorizationservice.data.repository;

import com.postech.authorizationservice.data.document.ClientDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ClientRepositoryITest extends TestContainerConfig {
    @Autowired
    private IClientRepository repository;

    @Test
    void shouldFindByClientId() {
        // Given
        ClientDocument sampleClient = this.repository.save(createSampleClient());
        UUID sampleUserId = sampleClient.getId();

        // When
        Optional<ClientDocument> foundClient = this.repository.findByClientId(sampleClient.getClientId());

        // Then
        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getId()).isEqualTo(sampleUserId);
    }

    private static ClientDocument createSampleClient() {
        return ClientDocument
                .builder()
                .id(UUID.randomUUID())
                .clientId("clientId")
                .clientSecret("clientSecret")
                .build();
    }
}
