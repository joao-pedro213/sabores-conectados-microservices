package com.postech.identityservice.data;

import com.postech.identityservice.data.document.IdentityDocument;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGeneratorCallback implements BeforeConvertCallback<IdentityDocument> {
    @Override
    public IdentityDocument onBeforeConvert(IdentityDocument accountDocument, String collection) {
        if (accountDocument.getId() == null) {
            accountDocument.setId(UUID.randomUUID());
        }
        return accountDocument;
    }
}
