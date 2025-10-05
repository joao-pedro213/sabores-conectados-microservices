package com.postech.reservationservice.data.repository;

import com.postech.reservationservice.data.document.ReservationDocument;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGeneratorEventListener extends AbstractMongoEventListener<ReservationDocument> {
    @Override
    public void onBeforeConvert(BeforeConvertEvent<ReservationDocument> event) {
        ReservationDocument document = event.getSource();
        if (document.getId() == null) {
            document.setId(UUID.randomUUID());
        }
    }
}
