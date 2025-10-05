package com.postech.core.account.gateway;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.dto.AccountDto;
import com.postech.core.account.datasource.IAccountDataSource;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class AccountGateway {
    private final IAccountDataSource dataSource;

    public static AccountGateway build(IAccountDataSource dataSource) {
        return new AccountGateway(dataSource);
    }

    public AccountEntity save(AccountEntity accountEntity) {
        AccountDto accountToSave = this.toDto(accountEntity);
        AccountDto savedAccount = this.dataSource.save(accountToSave);
        return this.toDomain(savedAccount);
    }

    public Optional<AccountEntity> findById(UUID id) {
        Optional<AccountDto> foundAccount = this.dataSource.findById(id);
        return foundAccount.map(this::toDomain);
    }

    public Optional<AccountEntity> findByIdentityId(UUID identityId) {
        Optional<AccountDto> foundAccount = this.dataSource.findByIdentityId(identityId);
        return foundAccount.map(this::toDomain);
    }

    public void deleteById(UUID id) {
        this.dataSource.deleteById(id);
    }

    private AccountDto toDto(AccountEntity accountEntity) {
        return AccountDto
                .builder()
                .id(accountEntity.getId())
                .identityId(accountEntity.getIdentityId())
                .name(accountEntity.getName())
                .email(accountEntity.getEmail())
                .address(accountEntity.getAddress())
                .lastUpdated(accountEntity.getLastUpdated())
                .build();
    }

    private AccountEntity toDomain(AccountDto accountDto) {
        return AccountEntity
                .builder()
                .id(accountDto.getId())
                .identityId(accountDto.getIdentityId())
                .name(accountDto.getName())
                .email(accountDto.getEmail())
                .address(accountDto.getAddress())
                .lastUpdated(accountDto.getLastUpdated())
                .build();
    }
}
