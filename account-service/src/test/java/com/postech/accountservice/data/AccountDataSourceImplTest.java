package com.postech.accountservice.data;

import com.postech.accountservice.data.document.AccountDocument;
import com.postech.accountservice.mapper.IAccountMapper;
import com.postech.accountservice.data.repository.IAccountRepository;
import com.postech.core.account.dto.AccountDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountDataSourceImplTest {
    @Mock
    private IAccountRepository mockRepository;

    @Mock
    private IAccountMapper mockAccountMapper;

    @InjectMocks
    private AccountDataSourceImpl dataSource;

    private static final UUID ID = UUID.randomUUID();

    private static final UUID IDENTITY_ID = UUID.randomUUID();

    @Test
    void shouldSaveAccount() {
        // Given
        final AccountDto accountToSaveDto = AccountDto.builder().build();
        final AccountDocument accountToSave = AccountDocument.builder().build();
        final AccountDto expectedSavedAccountDto = AccountDto.builder().build();
        when(this.mockAccountMapper.toAccountDocument(accountToSaveDto)).thenReturn(accountToSave);
        when(this.mockRepository.save(accountToSave)).thenReturn(accountToSave);
        when(this.mockAccountMapper.toAccountDto(accountToSave)).thenReturn(expectedSavedAccountDto);

        // When
        final AccountDto savedAccountDto = this.dataSource.save(accountToSaveDto);

        // Then
        assertThat(savedAccountDto).isNotNull().isEqualTo(expectedSavedAccountDto);
    }

    @Test
    void shouldFindAccountById() {
        // Given
        final AccountDocument foundAccount = AccountDocument.builder().build();
        when(this.mockRepository.findById(ID)).thenReturn(Optional.of(foundAccount));
        final AccountDto mappedAccountDto = AccountDto.builder().build();
        when(this.mockAccountMapper.toAccountDto(foundAccount)).thenReturn(mappedAccountDto);

        // When
        Optional<AccountDto> foundAccountDto = this.dataSource.findById(ID);

        // Then
        verify(this.mockRepository, times(1)).findById(ID);
        assertThat(foundAccountDto).isPresent().contains(mappedAccountDto);
    }

    @Test
    void shouldFindAccountByIdWhenRepositoryReturnsEmpty() {
        // Given
        when(this.mockRepository.findById(ID)).thenReturn(Optional.empty());

        // When
        Optional<AccountDto> foundAccountDto = this.dataSource.findById(ID);

        // Then
        assertThat(foundAccountDto).isNotPresent();
    }

    @Test
    void shouldFindAccountByIdentityId() {
        // Given
        final AccountDocument foundAccount = AccountDocument.builder().build();
        when(this.mockRepository.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.of(foundAccount));
        final AccountDto mappedAccountDto = AccountDto.builder().build();
        when(this.mockAccountMapper.toAccountDto(foundAccount)).thenReturn(mappedAccountDto);

        // When
        Optional<AccountDto> foundAccountDto = this.dataSource.findByIdentityId(IDENTITY_ID);

        // Then
        assertThat(foundAccountDto).isPresent().contains(mappedAccountDto);
    }

    @Test
    void shouldFindAccountByIdentityIdWhenRepositoryReturnsEmpty() {
        // Given
        when(this.mockRepository.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.empty());

        // When
        Optional<AccountDto> foundAccountDto = this.dataSource.findByIdentityId(IDENTITY_ID);

        // Then
        assertThat(foundAccountDto).isNotPresent();
    }

    @Test
    void shouldDeleteAccountById() {
        // When
        this.dataSource.deleteById(ID);

        // Then
        verify(this.mockRepository, times(1)).deleteById(ID);
    }
}
