package com.postech.reservationservice.data;

import com.postech.core.reservation.datasource.IReservationDataSource;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.dto.ReservationDto;
import com.postech.reservationservice.data.document.ReservationDocument;
import com.postech.reservationservice.data.repository.IReservationRepository;
import com.postech.reservationservice.mapper.IReservationMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@AllArgsConstructor
public class ReservationDataSourceImpl implements IReservationDataSource {
    private final IReservationRepository repository;
    private final IReservationMapper mapper;

    @Override
    public Mono<ReservationDto> save(ReservationDto reservationDto) {
        ReservationDocument reservationToSave = this.mapper.toReservationDocument(reservationDto);
        return this.repository.save(reservationToSave).map(this.mapper::toReservationDto);
    }

    @Override
    public Flux<ReservationDto> findAllByRestaurantId(UUID restaurantId) {
        return this.repository.findAllByRestaurantId(restaurantId).map(this.mapper::toReservationDto);
    }

    @Override
    public Flux<ReservationDto> findAllByRestaurantIdAndStatus(UUID restaurantId, ReservationStatus status) {
        return this.repository.findAllByRestaurantIdAndStatus(restaurantId, status).map(this.mapper::toReservationDto);
    }

    @Override
    public Mono<ReservationDto> findById(UUID id) {
        return this.repository.findById(id).map(this.mapper::toReservationDto);
    }
}
