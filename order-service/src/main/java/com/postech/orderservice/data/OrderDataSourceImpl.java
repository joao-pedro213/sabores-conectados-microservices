package com.postech.orderservice.data;

import com.postech.core.order.datasource.IOrderDataSource;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.dto.OrderDto;
import com.postech.orderservice.data.document.OrderDocument;
import com.postech.orderservice.data.repository.IOrderRepository;
import com.postech.orderservice.mapper.IOrderMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@AllArgsConstructor
public class OrderDataSourceImpl implements IOrderDataSource {
    private final IOrderRepository repository;
    private final IOrderMapper mapper;

    @Override
    public Mono<OrderDto> save(OrderDto orderDto) {
        OrderDocument orderToSave = this.mapper.toOrderDocument(orderDto);
        return this.repository.save(orderToSave).map(this.mapper::toOrderDto);
    }

    @Override
    public Flux<OrderDto> findAllByRestaurantId(UUID restaurantId) {
        return this.repository.findAllByRestaurantId(restaurantId).map(this.mapper::toOrderDto);
    }

    @Override
    public Flux<OrderDto> findAllByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status) {
        return this.repository.findAllByRestaurantIdAndStatus(restaurantId, status).map(this.mapper::toOrderDto);
    }

    @Override
    public Mono<OrderDto> findById(UUID id) {
        return this.repository.findById(id).map(this.mapper::toOrderDto);
    }
}
