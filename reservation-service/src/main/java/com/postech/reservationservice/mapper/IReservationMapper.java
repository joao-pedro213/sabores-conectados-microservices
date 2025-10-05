package com.postech.reservationservice.mapper;

import com.postech.core.reservation.dto.NewReservationDto;
import com.postech.core.reservation.dto.ReservationDto;
import com.postech.reservationservice.data.document.ReservationDocument;
import com.postech.reservationservice.dto.NewReservationRequestDto;
import com.postech.reservationservice.dto.ReservationResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IReservationMapper {

    @Mapping(target = "customerId", ignore = true)
    NewReservationDto toNewReservationDto(NewReservationRequestDto newReservationRequestDto);

    ReservationResponseDto toReservationResponseDto(ReservationDto reservationDto);

    ReservationDocument toReservationDocument(ReservationDto reservationDto);

    ReservationDto toReservationDto(ReservationDocument reservationDocument);
}
