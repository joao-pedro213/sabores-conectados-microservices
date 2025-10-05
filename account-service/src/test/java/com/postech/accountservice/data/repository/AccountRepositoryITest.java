package com.postech.accountservice.data.repository;

import com.postech.accountservice.api.config.AuditConfig;
import com.postech.accountservice.data.UUIDGeneratorCallback;
import com.postech.accountservice.data.document.AccountDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({UUIDGeneratorCallback.class, AuditConfig.class})
class AccountRepositoryITest extends TestContainerConfig {
    @Autowired
    private IAccountRepository repository;

    @Test
    void shouldFindByIdentityId() {
        // Given
        AccountDocument sampleAccount = this.repository.save(createSampleAccount());
        UUID sampleIdentityId = sampleAccount.getIdentityId();

        // When
        Optional<AccountDocument> foundAccount = this.repository.findByIdentityId(sampleIdentityId);

        // Then
        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getId()).isEqualTo(sampleAccount.getId());
        assertThat(foundAccount.get().getIdentityId()).isEqualTo(sampleIdentityId);
        assertThat(foundAccount.get().getCreatedAt()).isNotNull();
        assertThat(foundAccount.get().getLastUpdated()).isNotNull();
    }

    private static AccountDocument createSampleAccount() {
        return AccountDocument
                .builder()
                .identityId(UUID.randomUUID())
                .name("test manager")
                .email("test.manager@email.com")
                .address("test address")
                .build();
    }
}
