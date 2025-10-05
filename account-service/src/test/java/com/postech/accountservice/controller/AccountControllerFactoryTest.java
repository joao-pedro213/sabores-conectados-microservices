package com.postech.accountservice.controller;

import com.postech.accountservice.data.AccountDataSourceImpl;
import com.postech.core.account.controller.AccountController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AccountControllerFactoryTest {

    @Mock
    private AccountDataSourceImpl accountDataSourceImpl;

    @InjectMocks
    private AccountControllerFactory accountControllerFactory;

    @Test
    void shouldBuildUserControllerSuccessfully() {
        // Given
        AccountController expectedController = mock(AccountController.class);
        try (MockedStatic<AccountController> mockedStatic = Mockito.mockStatic(AccountController.class)) {
            mockedStatic.when(() -> AccountController.build(this.accountDataSourceImpl)).thenReturn(expectedController);

            // When
            AccountController actualController = this.accountControllerFactory.build();

            // Then
            assertThat(actualController).isNotNull().isEqualTo(expectedController);
        }
    }
}