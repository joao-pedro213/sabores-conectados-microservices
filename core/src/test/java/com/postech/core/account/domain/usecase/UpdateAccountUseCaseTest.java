package com.postech.core.account.domain.usecase;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.gateway.AccountGateway;
import com.postech.core.common.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAccountUseCaseTest {
    @Mock
    private AccountGateway mockAccountGateway;

    @InjectMocks
    private UpdateAccountUseCase useCase;

    @Test
    @DisplayName("should update an Account if it exists in the database")
    void shouldUpdateAccount() {
        // Given
        final UUID accountId = UUID.randomUUID();
        final String newName = "Augusto O.";
        final String newEmail = "augusto.01@example.com";
        final String newAddress = "9673 Bahringer Squares, Port Shylamouth, NE 32824-4680";
        final AccountEntity foundAccountEntity = AccountEntity.builder().email("augusto@example.com").build();
        when(this.mockAccountGateway.findById(accountId)).thenReturn(Optional.of(foundAccountEntity));
        final AccountEntity updatedAccountEntity = foundAccountEntity.toBuilder()
                .name(newName)
                .email(newEmail)
                .address(newAddress)
                .build();
        when(this.mockAccountGateway.save(any(AccountEntity.class))).thenReturn(updatedAccountEntity);

        // When
        final AccountEntity result = this.useCase.execute(accountId, newName, newEmail, newAddress);

        // Then
        assertThat(result).isNotNull().isEqualTo(updatedAccountEntity);
        final ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
        verify(this.mockAccountGateway, times(1)).save(captor.capture());
        final AccountEntity capturedAccountEntity = captor.getValue();
        assertThat(capturedAccountEntity.getName()).isEqualTo(newName);
        assertThat(capturedAccountEntity.getEmail()).isEqualTo(newEmail);
        assertThat(capturedAccountEntity.getAddress()).isEqualTo(newAddress);
    }

    @Test
    @DisplayName("should throw a EntityNotFoundException when the account is not found in the database")
    void shouldThrowEntityNotFoundException() {
        // Given
        final UUID accountId = UUID.randomUUID();
        when(this.mockAccountGateway.findById(accountId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> this.useCase.execute(accountId, "any name", "any@email.com", "any address"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
