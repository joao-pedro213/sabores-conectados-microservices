package com.postech.accountservice.mapper;

import com.postech.accountservice.data.document.AccountDocument;
import com.postech.accountservice.dto.AccountResponseDto;
import com.postech.accountservice.dto.NewAccountRequestDto;
import com.postech.accountservice.dto.UpdateAccountRequestDto;
import com.postech.core.account.dto.AccountDto;
import com.postech.core.account.dto.NewAccountDto;
import com.postech.core.account.dto.UpdateAccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IAccountMapper {
    AccountDocument toAccountDocument(AccountDto accountDto);

    AccountDto toAccountDto(AccountDocument accountDocument);

    NewAccountDto toNewAccountDto(NewAccountRequestDto newAccountRequestDto);

    UpdateAccountDto toUpdateAccountDto(UpdateAccountRequestDto updateAccountRequestDto);

    AccountResponseDto toAccountResponseDto(AccountDto accountDto);
}
