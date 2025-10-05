package com.postech.orderservice.data.repository;

import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.orderservice.data.document.OrderDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface IOrderRepository extends ReactiveMongoRepository<OrderDocument, UUID> {
    Flux<OrderDocument> findAllByRestaurantId(UUID restaurantId);

    Flux<OrderDocument> findAllByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status);
}
