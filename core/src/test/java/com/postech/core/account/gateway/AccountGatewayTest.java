package com.postech.core.account.gateway;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.dto.AccountDto;
import com.postech.core.account.datasource.IAccountDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountGatewayTest {

    @Mock
    private IAccountDataSource dataSource;

    @InjectMocks
    private AccountGateway gateway;

    private static final UUID ID = UUID.randomUUID();
    private static final UUID IDENTITY_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATED = LocalDateTime.now();

    @Test
    void shouldSaveAccount() {
        // Given
        AccountEntity accountEntityToSave = AccountEntity.builder()
                .id(ID)
                .identityId(IDENTITY_ID)
                .name("Luan")
                .email("luan@restaurant.com")
                .address("99897 Johnson Mountain")
                .lastUpdated(LAST_UPDATED)
                .build();

        AccountDto accountDtoToSave = AccountDto.builder()
                .id(ID)
                .identityId(IDENTITY_ID)
                .name("Luan")
                .email("luan@restaurant.com")
                .address("99897 Johnson Mountain")
                .lastUpdated(LAST_UPDATED)
                .build();

        when(dataSource.save(any(AccountDto.class))).thenReturn(accountDtoToSave);

        // When
        AccountEntity savedAccountEntity = gateway.save(accountEntityToSave);

        // Then
        ArgumentCaptor<AccountDto> argument = ArgumentCaptor.forClass(AccountDto.class);
        verify(dataSource, times(1)).save(argument.capture());
        AccountDto capturedAccountDto = argument.getValue();

        assertThat(capturedAccountDto).usingRecursiveComparison().isEqualTo(accountDtoToSave);
        assertThat(savedAccountEntity).usingRecursiveComparison().isEqualTo(accountEntityToSave);
    }

    @Test
    void shouldFindAccountById() {
        // Given
        AccountDto foundAccountDto = AccountDto.builder()
                .id(ID)
                .identityId(IDENTITY_ID)
                .name("Luan")
                .email("luan@restaurant.com")
                .address("99897 Johnson Mountain")
                .lastUpdated(LAST_UPDATED)
                .build();
        when(dataSource.findById(ID)).thenReturn(Optional.of(foundAccountDto));

        // When
        Optional<AccountEntity> foundAccount = gateway.findById(ID);

        // Then
        verify(dataSource, times(1)).findById(ID);
        assertThat(foundAccount).isPresent();
        AccountEntity expectedFoundAccountEntity = AccountEntity.builder()
                .id(ID)
                .identityId(IDENTITY_ID)
                .name("Luan")
                .email("luan@restaurant.com")
                .address("99897 Johnson Mountain")
                .lastUpdated(LAST_UPDATED)
                .build();
        assertThat(foundAccount.get()).usingRecursiveComparison().isEqualTo(expectedFoundAccountEntity);
    }

    @Test
    void shouldFindAccountByIdentityId() {
        // Given
        AccountDto foundAccountDto = AccountDto.builder()
                .id(ID)
                .identityId(IDENTITY_ID)
                .name("Luan")
                .email("luan@restaurant.com")
                .address("99897 Johnson Mountain")
                .lastUpdated(LAST_UPDATED)
                .build();
        when(dataSource.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.of(foundAccountDto));

        // When
        Optional<AccountEntity> foundAccount = gateway.findByIdentityId(IDENTITY_ID);

        // Then
        verify(dataSource, times(1)).findByIdentityId(IDENTITY_ID);
        assertThat(foundAccount).isPresent();
        AccountEntity expectedFoundAccountEntity = AccountEntity.builder()
                .id(ID)
                .identityId(IDENTITY_ID)
                .name("Luan")
                .email("luan@restaurant.com")
                .address("99897 Johnson Mountain")
                .lastUpdated(LAST_UPDATED)
                .build();
        assertThat(foundAccount.get()).usingRecursiveComparison().isEqualTo(expectedFoundAccountEntity);
    }

    @Test
    void shouldDeleteAccountById() {
        // When
        gateway.deleteById(ID);

        // Then
        verify(dataSource, times(1)).deleteById(ID);
    }
}
