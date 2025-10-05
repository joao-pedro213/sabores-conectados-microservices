package com.postech.accountservice.controller;

import com.postech.accountservice.data.AccountDataSourceImpl;
import com.postech.core.account.controller.AccountController;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountControllerFactory {
    private final AccountDataSourceImpl userDataSourceJpa;

    public AccountController build() {
        return AccountController.build(this.userDataSourceJpa);
    }
}
