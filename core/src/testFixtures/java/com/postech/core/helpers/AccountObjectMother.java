package com.postech.core.helpers;

import com.postech.core.account.domain.entity.AccountEntity;
import com.postech.core.account.dto.AccountDto;
import com.postech.core.account.dto.NewAccountDto;
import com.postech.core.account.dto.UpdateAccountDto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class AccountObjectMother {

    public static AccountEntity buildSampleAccount(Map<String, Object> sampleData) {
        return AccountEntity
                .builder()
                .id(
                        sampleData.get("id") == null
                                ? UUID.randomUUID()
                                : UUID.fromString(sampleData.get("id").toString()))
                .identityId(
                        sampleData.get("identityId") == null
                                ? UUID.randomUUID()
                                : UUID.fromString(sampleData.get("identityId").toString()))
                .name(sampleData.get("name").toString())
                .email(sampleData.get("email").toString())
                .address(sampleData.get("address").toString())
                .lastUpdated(
                        sampleData.get("lastUpdated") == null
                                ? LocalDateTime.now()
                                : LocalDateTime.parse(sampleData.get("lastUpdated").toString()))
                .build();
    }

    public static AccountDto buildSampleAccountDto(Map<String, Object> sampleData) {
        return AccountDto
                .builder()
                .id(
                        sampleData.get("id") == null
                                ? UUID.randomUUID()
                                : UUID.fromString(sampleData.get("id").toString()))
                .identityId(
                        sampleData.get("identityId") == null
                                ? UUID.randomUUID()
                                : UUID.fromString(sampleData.get("identityId").toString()))
                .name(sampleData.get("name").toString())
                .email(sampleData.get("email").toString())
                .address(sampleData.get("address").toString())
                .lastUpdated(
                        sampleData.get("lastUpdated") == null
                                ? LocalDateTime.now()
                                : LocalDateTime.parse(sampleData.get("lastUpdated").toString()))
                .build();
    }

    public static NewAccountDto buildSampleNewAccountDto(Map<String, Object> sampleData) {
        return NewAccountDto
                .builder()
                .identityId(
                        sampleData.get("identityId") == null
                                ? UUID.randomUUID()
                                : UUID.fromString(sampleData.get("identityId").toString()))
                .name(sampleData.get("name").toString())
                .email(sampleData.get("email").toString())
                .address(sampleData.get("address").toString())
                .build();
    }

    public static UpdateAccountDto buildSampleUpdateAccountDto(Map<String, Object> sampleData) {
        return UpdateAccountDto
                .builder()
                .name(sampleData.get("name").toString())
                .email(sampleData.get("email").toString())
                .address(sampleData.get("address").toString())
                .build();
    }
}
