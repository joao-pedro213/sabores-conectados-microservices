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

import java.util.UUID;

public class AccountController {
    private final AccountGateway accountGateway;

    public AccountController(IAccountDataSource accountDataSource) {
        this.accountGateway = AccountGateway.build(accountDataSource);
    }

    public static AccountController build(IAccountDataSource accountDataSource) {
        return new AccountController(accountDataSource);
    }

    public AccountDto createAccount(NewAccountDto newAccountDto) {
        AccountEntity newAccount = CreateAccountUseCase
                .build(this.accountGateway)
                .execute(this.toDomain(newAccountDto));
        return AccountPresenter.build().toDto(newAccount);
    }

    public AccountDto retrieveAccountById(UUID id) {
        AccountEntity foundAccount = RetrieveAccountByIdUseCase.build(this.accountGateway).execute(id);
        return AccountPresenter.build().toDto(foundAccount);
    }

    public AccountDto updateAccount(UUID id, UpdateAccountDto updateAccountDto) {
        AccountEntity updatedAccountEntity = UpdateAccountUseCase
                .build(this.accountGateway)
                .execute(id, updateAccountDto.getName(), updateAccountDto.getEmail(), updateAccountDto.getAddress());
        return AccountPresenter.build().toDto(updatedAccountEntity);
    }

    public void deleteAccountById(UUID id) {
        DeleteAccountByIdUseCase.build(this.accountGateway).execute(id);
    }

    private AccountEntity toDomain(NewAccountDto newAccountDto) {
        return AccountEntity
                .builder()
                .identityId(newAccountDto.getIdentityId())
                .name(newAccountDto.getName())
                .email(newAccountDto.getEmail())
                .address(newAccountDto.getAddress())
                .build();
    }
}
