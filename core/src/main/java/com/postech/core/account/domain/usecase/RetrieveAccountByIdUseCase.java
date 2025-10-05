package com.postech.core.account.domain.usecase;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.gateway.AccountGateway;
import com.postech.core.common.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class RetrieveAccountByIdUseCase {
    private final AccountGateway accountGateway;

    public static RetrieveAccountByIdUseCase build(AccountGateway accountGateway) {
        return new RetrieveAccountByIdUseCase(accountGateway);
    }

    public AccountEntity execute(UUID id) {
        return this.accountGateway.findById(id).orElseThrow(() -> new EntityNotFoundException("Account"));
    }
}
