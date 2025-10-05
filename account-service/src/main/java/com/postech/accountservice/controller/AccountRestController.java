package com.postech.accountservice.controller;

import com.postech.accountservice.dto.AccountResponseDto;
import com.postech.accountservice.dto.NewAccountRequestDto;
import com.postech.accountservice.dto.UpdateAccountRequestDto;
import com.postech.accountservice.mapper.IAccountMapper;
import com.postech.accountservice.service.IdentityService;
import com.postech.core.account.dto.AccountDto;
import com.postech.core.account.dto.NewAccountDto;
import com.postech.core.account.dto.UpdateAccountDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class AccountRestController {
    private final AccountControllerFactory controllerFactory;
    private final IAccountMapper mapper;
    private final IdentityService identityService;

    @PostMapping
    public ResponseEntity<AccountResponseDto> create(@Valid @RequestBody NewAccountRequestDto requestDto) {
        NewAccountDto newAccountDto = this.mapper.toNewAccountDto(requestDto);
        UUID identityId = this.identityService.create(requestDto.getIdentity());
        newAccountDto.setIdentityId(identityId);
        AccountDto accountDto = this.controllerFactory.build().createAccount(newAccountDto);
        AccountResponseDto accountResponseDto = this.mapper.toAccountResponseDto(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDto);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize(
            "(@securityService.isCustomer() or @securityService.isRestaurantOwner() or @securityService.isRestaurantManager())"
                    + " and hasAuthority('SCOPE_account:read')"
                    + " and @securityService.isResourceOwner(#id)")
    public ResponseEntity<AccountResponseDto> retrieveById(@PathVariable UUID id) {
        AccountDto accountDto = this.controllerFactory.build().retrieveAccountById(id);
        AccountResponseDto accountResponseDto = this.mapper.toAccountResponseDto(accountDto);
        return ResponseEntity.status(HttpStatus.OK).body(accountResponseDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "(@securityService.isCustomer() or @securityService.isRestaurantOwner() or @securityService.isRestaurantManager())"
                    + " and hasAuthority('SCOPE_account:write')"
                    + " and @securityService.isResourceOwner(#id)")
    public ResponseEntity<AccountResponseDto> updateById(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountRequestDto requestDto) {
        UpdateAccountDto updateAccountDto = this.mapper.toUpdateAccountDto(requestDto);
        AccountDto accountDto = this.controllerFactory.build().updateAccount(id, updateAccountDto);
        AccountResponseDto accountResponseDto = this.mapper.toAccountResponseDto(accountDto);
        return ResponseEntity.status(HttpStatus.OK).body(accountResponseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "(@securityService.isCustomer() or @securityService.isRestaurantOwner() or @securityService.isRestaurantManager())"
                    + " and hasAuthority('SCOPE_account:write')"
                    + " and @securityService.isResourceOwner(#id)")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        this.controllerFactory.build().deleteAccountById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
