package com.postech.identityservice.data.repository;

import com.postech.identityservice.api.config.AuditConfig;
import com.postech.identityservice.data.UUIDGeneratorCallback;
import com.postech.identityservice.data.document.IdentityDocument;
import com.postech.identityservice.data.document.enumerator.SystemRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
@Import({UUIDGeneratorCallback.class, AuditConfig.class})
class IdentityRepositoryITest extends TestContainerConfig {
    @Autowired
    private IIdentityRepository repository;

    @Test
    void shouldSaveAndFindById() {
        // Given
        IdentityDocument sampleIdentity = createSampleIdentity();

        // When
        IdentityDocument savedIdentity = this.repository.save(sampleIdentity);
        Optional<IdentityDocument> foundIdentity = this.repository.findById(savedIdentity.getId());

        // Then
        assertThat(foundIdentity).isPresent();
        assertThat(foundIdentity.get().getId()).isNotNull();
        assertThat(foundIdentity.get().getId()).isEqualTo(savedIdentity.getId());
        assertThat(foundIdentity.get().getUsername()).isEqualTo(sampleIdentity.getUsername());
        assertThat(foundIdentity.get().getRole()).isEqualTo(sampleIdentity.getRole());
        assertThat(foundIdentity.get().getAuthorities()).isEqualTo(sampleIdentity.getAuthorities());
        assertThat(foundIdentity.get().getCreatedAt()).isNotNull();
        assertThat(foundIdentity.get().getUpdatedAt()).isNotNull();
    }

    private static IdentityDocument createSampleIdentity() {
        return IdentityDocument
                .builder()
                .username("test-user")
                .password("secret!Password")
                .role(SystemRole.RESTAURANT_OWNER)
                .authorities(Set.of("identity:write"))
                .build();
    }
}
