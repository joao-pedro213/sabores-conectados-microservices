package com.postech.core.account.presenter;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.dto.AccountDto;

public class AccountPresenter {
    public static AccountPresenter build() {
        return new AccountPresenter();
    }

    public AccountDto toDto(AccountEntity accountEntity) {
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
}
