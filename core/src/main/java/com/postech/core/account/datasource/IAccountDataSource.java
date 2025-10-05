package com.postech.core.account.datasource;

import com.postech.core.account.dto.AccountDto;

import java.util.Optional;
import java.util.UUID;

public interface IAccountDataSource {
    AccountDto save(AccountDto accountDto);

    Optional<AccountDto> findById(UUID id);

    Optional<AccountDto> findByIdentityId(UUID id);

    void deleteById(UUID id);
}
