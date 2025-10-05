package com.postech.authorizationservice.data.repository;

import com.postech.authorizationservice.data.document.IdentityDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class IdentityRepositoryITest extends TestContainerConfig {
    @Autowired
    private IIdentityRepository repository;

    @Test
    void shouldFindByUsername() {
        // Given
        IdentityDocument sampleIdentity = this.repository.save(createSampleIdentity());
        UUID sampleIdentityId = sampleIdentity.getId();

        // When
        Optional<IdentityDocument> foundIdentity = this.repository.findByUsername(sampleIdentity.getUsername());

        // Then
        assertThat(foundIdentity).isPresent();
        assertThat(foundIdentity.get().getId()).isEqualTo(sampleIdentityId);
    }

    private static IdentityDocument createSampleIdentity() {
        return IdentityDocument
                .builder()
                .id(UUID.randomUUID())
                .username("manager-123")
                .password("strong!Password")
                .authorities(Set.of("RESTAURANT_MANAGER"))
                .build();
    }
}
