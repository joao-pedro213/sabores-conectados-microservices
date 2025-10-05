package com.postech.reservationservice.data.document;

import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Document("reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDocument implements Persistable<UUID> {
    @Id
    private UUID id;
    private UUID restaurantId;
    private UUID customerId;
    private ReservationStatus status;
    private int people;
    private LocalDate date;
    private LocalTime time;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
