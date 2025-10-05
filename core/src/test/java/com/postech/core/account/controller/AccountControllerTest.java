package com.postech.core.account.controller;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.domain.usecase.CreateAccountUseCase;
import com.postech.core.account.domain.usecase.DeleteAccountByIdUseCase;
import com.postech.core.account.domain.usecase.RetrieveAccountByIdUseCase;
import com.postech.core.account.domain.usecase.UpdateAccountUseCase;
import com.postech.core.account.dto.AccountDto;
import com.postech.core.account.dto.NewAccountDto;
import com.postech.core.account.dto.UpdateAccountDto;
import com.postech.core.account.gateway.AccountGateway;
import com.postech.core.account.presenter.AccountPresenter;
import com.postech.core.account.datasource.IAccountDataSource;
import com.postech.core.helpers.AccountObjectMother;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private IAccountDataSource accountDataSource;

    @InjectMocks
    private AccountController controller;

    @Mock
    private AccountPresenter accountPresenter;

    private MockedStatic<AccountPresenter> mockedStaticAccountPresenter;

    @BeforeEach
    void setUp() {
        this.mockedStaticAccountPresenter = mockStatic(AccountPresenter.class);
        when(AccountPresenter.build()).thenReturn(this.accountPresenter);
    }

    private static Map<String, Object> getSampleAccountData() {
        return Map.of(
                "id", UUID.randomUUID(),
                "identityId", UUID.randomUUID(),
                "name", "Marcos",
                "email", "marcos@mail.com",
                "address", "82495 Xavier Keys, Emersonburgh, KS 65336-8213",
                "lastUpdated", LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateAccount() {
        // Given
        final Map<String, Object> sampleData = getSampleAccountData();
        final NewAccountDto newAccountDto = AccountObjectMother.buildSampleNewAccountDto(sampleData);
        final AccountEntity createdAccountEntity = AccountObjectMother.buildSampleAccount(sampleData);
        final AccountDto createdAccountDto = AccountDto.builder().build();
        final CreateAccountUseCase mockCreateAccountUseCase = mock(CreateAccountUseCase.class);
        when(mockCreateAccountUseCase.execute(any(AccountEntity.class))).thenReturn(createdAccountEntity);
        when(this.accountPresenter.toDto(createdAccountEntity)).thenReturn(createdAccountDto);
        try (MockedStatic<CreateAccountUseCase> mockedStatic = mockStatic(CreateAccountUseCase.class)) {
            mockedStatic.when(() -> CreateAccountUseCase.build(any(AccountGateway.class))).thenReturn(mockCreateAccountUseCase);

            // When
            final AccountDto result = this.controller.createAccount(newAccountDto);

            // Then
            final ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
            verify(mockCreateAccountUseCase).execute(captor.capture());
            final AccountEntity expectedEntity = AccountObjectMother.buildSampleAccount(sampleData).toBuilder().id(null).lastUpdated(null).build();
            assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedEntity);
            assertThat(result).isEqualTo(createdAccountDto);
        }
    }

    @Test
    void shouldRetrieveAccountById() {
        // Given
        final Map<String, Object> sampleData = getSampleAccountData();
        final UUID accountId = (UUID) sampleData.get("id");
        final AccountEntity foundAccountEntity = AccountObjectMother.buildSampleAccount(sampleData);
        final AccountDto foundAccountDto = AccountDto.builder().build();
        final RetrieveAccountByIdUseCase mockUseCase = mock(RetrieveAccountByIdUseCase.class);
        when(mockUseCase.execute(accountId)).thenReturn(foundAccountEntity);
        when(this.accountPresenter.toDto(foundAccountEntity)).thenReturn(foundAccountDto);
        try (MockedStatic<RetrieveAccountByIdUseCase> mockedStatic = mockStatic(RetrieveAccountByIdUseCase.class)) {
            mockedStatic.when(() -> RetrieveAccountByIdUseCase.build(any(AccountGateway.class))).thenReturn(mockUseCase);

            // When
            final AccountDto result = this.controller.retrieveAccountById(accountId);

            // Then
            verify(mockUseCase).execute(accountId);
            assertThat(result).isEqualTo(foundAccountDto);
        }
    }

    @Test
    void shouldUpdateAccount() {
        // Given
        final Map<String, Object> sampleData = getSampleAccountData();
        final UUID accountId = (UUID) sampleData.get("id");
        final UpdateAccountDto updateAccountDto = AccountObjectMother.buildSampleUpdateAccountDto(sampleData);
        final AccountEntity updatedAccountEntity = AccountObjectMother.buildSampleAccount(sampleData);
        final AccountDto updatedAccountDto = AccountDto.builder().build();

        final UpdateAccountUseCase mockUseCase = mock(UpdateAccountUseCase.class);
        when(mockUseCase.execute(accountId, updateAccountDto.getName(), updateAccountDto.getEmail(), updateAccountDto.getAddress())).thenReturn(updatedAccountEntity);
        when(this.accountPresenter.toDto(updatedAccountEntity)).thenReturn(updatedAccountDto);

        try (MockedStatic<UpdateAccountUseCase> mockedStatic = mockStatic(UpdateAccountUseCase.class)) {
            mockedStatic.when(() -> UpdateAccountUseCase.build(any(AccountGateway.class))).thenReturn(mockUseCase);

            // When
            final AccountDto result = this.controller.updateAccount(accountId, updateAccountDto);

            // Then
            verify(mockUseCase).execute(accountId, updateAccountDto.getName(), updateAccountDto.getEmail(), updateAccountDto.getAddress());
            assertThat(result).isEqualTo(updatedAccountDto);
        }
    }

    @Test
    void shouldDeleteAccountById() {
        // Given
        final Map<String, Object> sampleData = getSampleAccountData();
        final UUID accountId = (UUID) sampleData.get("id");
        final DeleteAccountByIdUseCase mockUseCase = mock(DeleteAccountByIdUseCase.class);

        try (MockedStatic<DeleteAccountByIdUseCase> mockedStatic = mockStatic(DeleteAccountByIdUseCase.class)) {
            mockedStatic.when(() -> DeleteAccountByIdUseCase.build(any(AccountGateway.class))).thenReturn(mockUseCase);

            // When
            this.controller.deleteAccountById(accountId);

            // Then
            verify(mockUseCase).execute(accountId);
        }
    }

    @AfterEach
    void tearDown() {
        this.mockedStaticAccountPresenter.close();
    }
}
