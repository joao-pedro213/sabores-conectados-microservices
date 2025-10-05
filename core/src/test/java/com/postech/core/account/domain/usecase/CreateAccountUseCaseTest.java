package com.postech.core.account.domain.usecase;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.gateway.AccountGateway;
import com.postech.core.common.exception.EntityAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {
    @Mock
    private AccountGateway mockAccountGateway;

    @InjectMocks
    private CreateAccountUseCase useCase;

    @Test
    @DisplayName("Should create a new Account if it doesn't exist in the database yet")
    void shouldCreateAccount() {
        // Given
        final AccountEntity newAccountEntity = AccountEntity
                .builder()
                .identityId(UUID.randomUUID())
                .email("test_user@example.com")
                .build();
        when(this.mockAccountGateway.findByIdentityId(newAccountEntity.getIdentityId())).thenReturn(Optional.empty());
        when(this.mockAccountGateway.save(newAccountEntity)).thenReturn(newAccountEntity);

        // When
        final AccountEntity accountEntity = this.useCase.execute(newAccountEntity);

        // Then
        assertThat(accountEntity).isNotNull().isEqualTo(newAccountEntity);
    }

    @Test
    @DisplayName("Should throw a EntityAlreadyExistsException when the new account is found in the database before its creation")
    void shouldThrowAccountAlreadyExist() {
        // Given
        final AccountEntity newAccountEntity = AccountEntity
                .builder()
                .identityId(UUID.randomUUID())
                .email("test_user@example.com")
                .build();
        when(this.mockAccountGateway.findByIdentityId(newAccountEntity.getIdentityId())).thenReturn(Optional.of(newAccountEntity));

        // When & Then
        assertThatThrownBy(() -> this.useCase.execute(newAccountEntity)).isInstanceOf(EntityAlreadyExistsException.class);
    }
}
