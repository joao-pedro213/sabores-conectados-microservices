package com.postech.core.account.presenter;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.dto.AccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountPresenterTest {
    private AccountPresenter presenter;

    @BeforeEach
    void setUp() {
        this.presenter = AccountPresenter.build();
    }

    @Test
    void shouldMapDomainToDto() {
        // Given
        final UUID id = UUID.randomUUID();
        final UUID identityId = UUID.randomUUID();
        final LocalDateTime lastUpdated = LocalDateTime.now();
        final AccountEntity accountEntity = AccountEntity.builder()
                .id(id)
                .identityId(identityId)
                .name("Laila")
                .email("laila@pizza.com")
                .address("59747 Hilpert Mountains, West Nedmouth, LA 71306")
                .lastUpdated(lastUpdated)
                .build();

        // When
        final AccountDto accountDto = this.presenter.toDto(accountEntity);

        // Then
        final AccountDto expectedAccountDto = AccountDto.builder()
                .id(id)
                .identityId(identityId)
                .name("Laila")
                .email("laila@pizza.com")
                .address("59747 Hilpert Mountains, West Nedmouth, LA 71306")
                .lastUpdated(lastUpdated)
                .build();
        assertThat(accountDto).usingRecursiveComparison().isEqualTo(expectedAccountDto);
    }
}
