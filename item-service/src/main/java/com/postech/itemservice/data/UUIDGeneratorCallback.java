package com.postech.itemservice.data;

import com.postech.itemservice.data.document.ItemDocument;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGeneratorCallback implements BeforeConvertCallback<ItemDocument> {
    @Override
    public ItemDocument onBeforeConvert(ItemDocument itemDocument, String collection) {
        if (itemDocument.getId() == null) {
            itemDocument.setId(UUID.randomUUID());
        }
        return itemDocument;
    }
}
