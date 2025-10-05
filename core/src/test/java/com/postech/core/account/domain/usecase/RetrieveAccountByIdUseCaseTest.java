package com.postech.core.account.domain.usecase;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.gateway.AccountGateway;
import com.postech.core.common.exception.EntityNotFoundException;
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
class RetrieveAccountByIdUseCaseTest {
    @Mock
    private AccountGateway mockAccountGateway;

    @InjectMocks
    private RetrieveAccountByIdUseCase useCase;

    private static final UUID ID = UUID.randomUUID();

    @Test
    @DisplayName("Should find a User if it exists in the database")
    void shouldFindUserById() {
        // Given
        final AccountEntity foundAccountEntity = AccountEntity
                .builder()
                .id(ID)
                .email("test_user@example.com")
                .build();
        when(this.mockAccountGateway.findById(ID)).thenReturn(Optional.of(foundAccountEntity));

        // When
        final AccountEntity accountEntity = this.useCase.execute(ID);

        // Then
        assertThat(accountEntity).isNotNull().isEqualTo(foundAccountEntity);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when the user is not found in the database")
    void shouldThrowEntityNotFoundException() {
        // Given
        when(this.mockAccountGateway.findById(ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> this.useCase.execute(ID)).isInstanceOf(EntityNotFoundException.class);
    }
}
