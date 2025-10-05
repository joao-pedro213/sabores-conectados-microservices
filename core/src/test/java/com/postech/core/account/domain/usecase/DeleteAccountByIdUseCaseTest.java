package com.postech.core.account.domain.usecase;

import com.postech.core.account.gateway.AccountGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteAccountByIdUseCaseTest {
    @Mock
    private AccountGateway mockAccountGateway;

    @InjectMocks
    private DeleteAccountByIdUseCase useCase;

    private static final UUID ID = UUID.randomUUID();

    @Test
    @DisplayName("Should delete a Account from the database")
    void shouldDeleteAccountById() {
        // When
        this.useCase.execute(ID);

        // Then
        verify(this.mockAccountGateway, times(1)).deleteById(ID);
    }
}
