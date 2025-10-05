package com.postech.orderservice.data.repository;


import com.postech.orderservice.data.document.OrderDocument;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGeneratorEventListener extends AbstractMongoEventListener<OrderDocument> {
    @Override
    public void onBeforeConvert(BeforeConvertEvent<OrderDocument> event) {
        OrderDocument document = event.getSource();
        if (document.getId() == null) {
            document.setId(UUID.randomUUID());
        }
    }
}
