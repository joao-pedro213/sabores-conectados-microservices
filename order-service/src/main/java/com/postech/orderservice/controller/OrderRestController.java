package com.postech.orderservice.controller;

import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.dto.NewOrderDto;
import com.postech.orderservice.dto.ChangeStatusRequestDto;
import com.postech.orderservice.dto.NewOrderRequestDto;
import com.postech.orderservice.dto.OrderResponseDto;
import com.postech.orderservice.mapper.IOrderMapper;
import com.postech.orderservice.service.SecurityService;
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
public class OrderRestController {
    private final OrderControllerFactory orderControllerFactory;
    private final IOrderMapper mapper;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("@securityService.canCreateOrder()")
    public Mono<ResponseEntity<OrderResponseDto>> create(@Valid @RequestBody NewOrderRequestDto requestDto) {
        return this.securityService
                .getIdentityFromSecurityContext()
                .map(customerId -> {
                    NewOrderDto newOrderDto = this.mapper.toNewOrderDto(requestDto);
                    newOrderDto.setCustomerId(customerId);
                    return newOrderDto;
                })
                .flatMap(newOrderDto ->
                        this.orderControllerFactory
                                .build()
                                .createOrder(newOrderDto)
                                .map(orderDto ->
                                        ResponseEntity
                                                .status(HttpStatus.OK)
                                                .body(this.mapper.toOrderResponseDto(orderDto))));
    }

    @GetMapping("/restaurant/{restaurantId}/list")
    @PreAuthorize("@securityService.canReadRestaurant(#restaurantId)")
    public Mono<ResponseEntity<List<OrderResponseDto>>> retrieveByRestaurantId(
            @PathVariable UUID restaurantId,
            @RequestParam(value = "status", required = false) OrderStatus status) {
        return this.orderControllerFactory
                .build()
                .retrieveOrdersByRestaurantId(restaurantId, status)
                .map(this.mapper::toOrderResponseDto)
                .collectList()
                .map(orderResponseDtos ->
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .body(orderResponseDtos));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@securityService.canChangeOrderStatus(#id)")
    public Mono<ResponseEntity<OrderResponseDto>> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeStatusRequestDto requestDto) {
        return this.orderControllerFactory
                .build()
                .changeOrderStatus(id, requestDto.getNewStatus())
                .map(orderDto -> ResponseEntity.status(HttpStatus.OK).body(this.mapper.toOrderResponseDto(orderDto)));
    }
}
