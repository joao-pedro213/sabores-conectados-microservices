package com.postech.core.account.domain.usecase;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.gateway.AccountGateway;
import com.postech.core.common.exception.EntityAlreadyExistsException;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class CreateAccountUseCase {
    private final AccountGateway accountGateway;

    public static CreateAccountUseCase build(AccountGateway accountGateway) {
        return new CreateAccountUseCase(accountGateway);
    }

    public AccountEntity execute(AccountEntity accountEntity) {
        Optional<AccountEntity> foundAccount = this.accountGateway.findByIdentityId(accountEntity.getIdentityId());
        if (foundAccount.isPresent()) {
            throw new EntityAlreadyExistsException("Account");
        }
        return this.accountGateway.save(accountEntity);
    }
}
