package com.postech.core.account.domain.usecase;

import com.postech.core.account.gateway.AccountGateway;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class DeleteAccountByIdUseCase {
    private final AccountGateway accountGateway;

    public static DeleteAccountByIdUseCase build(AccountGateway accountGateway) {
        return new DeleteAccountByIdUseCase(accountGateway);
    }

    public void execute(UUID id) {
        this.accountGateway.deleteById(id);
    }
}
