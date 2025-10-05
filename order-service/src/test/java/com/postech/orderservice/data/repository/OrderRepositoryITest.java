package com.postech.orderservice.data.repository;

import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.orderservice.data.document.OrderDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

@DataMongoTest
class OrderRepositoryITest extends TestContainerConfig {

    @Autowired
    private IOrderRepository repository;

    @Test
    void shouldFindAllByRestaurantId() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        OrderDocument order1 = OrderDocument.builder().id(UUID.randomUUID()).restaurantId(restaurantId).build();
        OrderDocument order2 = OrderDocument.builder().id(UUID.randomUUID()).restaurantId(restaurantId).build();
        this.repository.saveAll(Flux.just(order1, order2)).blockLast();

        // When
        Flux<OrderDocument> result = this.repository.findAllByRestaurantId(restaurantId);

        // Then
        StepVerifier
                .create(result)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void shouldFindAllByRestaurantIdAndStatus() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        OrderStatus status = OrderStatus.PENDING;
        OrderDocument order1 = OrderDocument
                .builder()
                .id(UUID.randomUUID())
                .restaurantId(restaurantId)
                .status(status)
                .build();
        OrderDocument order2 = OrderDocument
                .builder()
                .id(UUID.randomUUID())
                .restaurantId(restaurantId)
                .status(status)
                .build();
        OrderDocument order3 = OrderDocument
                .builder()
                .id(UUID.randomUUID())
                .restaurantId(restaurantId)
                .status(OrderStatus.ACCEPTED)
                .build();
        this.repository.saveAll(Flux.just(order1, order2, order3)).blockLast();

        // When
        Flux<OrderDocument> result = this.repository.findAllByRestaurantIdAndStatus(restaurantId, status);

        // Then
        StepVerifier
                .create(result)
                .expectNextCount(2)
                .verifyComplete();
    }
}
