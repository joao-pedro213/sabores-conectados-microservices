package com.postech.core.account.domain.usecase;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.gateway.AccountGateway;
import com.postech.core.common.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class UpdateAccountUseCase {
    private final AccountGateway accountGateway;

    public static UpdateAccountUseCase build(AccountGateway accountGateway) {
        return new UpdateAccountUseCase(accountGateway);
    }

    public AccountEntity execute(UUID id, String name, String email, String address) {
        AccountEntity foundAccount = this.accountGateway
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account"));
        AccountEntity accountWithUpdates = foundAccount
                .toBuilder()
                .name(name)
                .email(email)
                .address(address)
                .build();
        return this.accountGateway.save(accountWithUpdates);
    }
}
