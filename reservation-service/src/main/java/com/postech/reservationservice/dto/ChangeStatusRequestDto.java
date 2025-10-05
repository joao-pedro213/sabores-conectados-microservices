package com.postech.reservationservice.dto;

import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ChangeStatusRequestDto {
    private ReservationStatus newStatus;
}
