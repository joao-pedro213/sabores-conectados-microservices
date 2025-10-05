package com.postech.reservationservice.controller;

import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.dto.NewReservationDto;
import com.postech.reservationservice.dto.ChangeStatusRequestDto;
import com.postech.reservationservice.dto.NewReservationRequestDto;
import com.postech.reservationservice.dto.ReservationResponseDto;
import com.postech.reservationservice.mapper.IReservationMapper;
import com.postech.reservationservice.service.SecurityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ReservationRestController {
    private final ReservationControllerFactory reservationControllerFactory;
    private final IReservationMapper mapper;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("@securityService.canCreateReservation()")
    public Mono<ResponseEntity<ReservationResponseDto>> create(@Valid @RequestBody NewReservationRequestDto requestDto) {
        return this.securityService
                .getIdentityFromSecurityContext()
                .map(customerId -> {
                    NewReservationDto newReservationDto = this.mapper.toNewReservationDto(requestDto);
                    newReservationDto.setCustomerId(customerId);
                    return newReservationDto;
                })
                .flatMap(newReservationDto ->
                        this.reservationControllerFactory
                                .build()
                                .createReservation(newReservationDto)
                                .map(reservationDto ->
                                        ResponseEntity
                                                .status(HttpStatus.OK)
                                                .body(this.mapper.toReservationResponseDto(reservationDto))));
    }

    @GetMapping("/restaurant/{restaurantId}/list")
    @PreAuthorize("@securityService.canReadRestaurant(#restaurantId)")
    public Mono<ResponseEntity<List<ReservationResponseDto>>> retrieveByRestaurantId(
            @PathVariable UUID restaurantId,
            @RequestParam(value = "status", required = false) ReservationStatus status) {
        return this.reservationControllerFactory
                .build()
                .retrieveReservationByRestaurantId(restaurantId, status)
                .map(this.mapper::toReservationResponseDto)
                .collectList()
                .map(reservationResponseDtos ->
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .body(reservationResponseDtos));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@securityService.canChangeReservationStatus(#id)")
    public Mono<ResponseEntity<ReservationResponseDto>> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeStatusRequestDto requestDto) {
        return this.reservationControllerFactory
                .build()
                .changeReservationStatus(id, requestDto.getNewStatus())
                .map(orderDto ->
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .body(this.mapper.toReservationResponseDto(orderDto)));
    }
}
