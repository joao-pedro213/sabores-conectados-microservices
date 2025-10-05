package com.postech.accountservice.data;

import com.postech.accountservice.data.document.AccountDocument;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGeneratorCallback implements BeforeConvertCallback<AccountDocument> {
    @Override
    public AccountDocument onBeforeConvert(AccountDocument accountDocument, String collection) {
        if (accountDocument.getId() == null) {
            accountDocument.setId(UUID.randomUUID());
        }
        return accountDocument;
    }
}
