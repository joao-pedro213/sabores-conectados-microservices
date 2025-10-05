package com.postech.accountservice.data;

import com.postech.accountservice.data.document.AccountDocument;
import com.postech.accountservice.mapper.IAccountMapper;
import com.postech.accountservice.data.repository.IAccountRepository;
import com.postech.core.account.dto.AccountDto;
import com.postech.core.account.datasource.IAccountDataSource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AccountDataSourceImpl implements IAccountDataSource {
    private final IAccountRepository repository;
    private final IAccountMapper mapper;

    @Override
    public AccountDto save(AccountDto accountDto) {
        AccountDocument userToSave = this.mapper.toAccountDocument(accountDto);
        AccountDocument savedUser = this.repository.save(userToSave);
        return this.mapper.toAccountDto(savedUser);
    }

    @Override
    public Optional<AccountDto> findById(UUID id) {
        Optional<AccountDocument> foundUser = this.repository.findById(id);
        return foundUser.map(this.mapper::toAccountDto);
    }

    @Override
    public Optional<AccountDto> findByIdentityId(UUID identityId) {
        Optional<AccountDocument> foundUser = this.repository.findByIdentityId(identityId);
        return foundUser.map(this.mapper::toAccountDto);
    }

    @Override
    public void deleteById(UUID id) {
        this.repository.deleteById(id);
    }
}
