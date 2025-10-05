package com.postech.core.order.domain.usecase;

import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.gateway.OrderGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveOrdersByRestaurantIdUseCaseTest {

    @Mock
    private OrderGateway mockOrderGateway;

    @InjectMocks
    private RetrieveOrdersByRestaurantIdUseCase useCase;

    @Test
    @DisplayName("Should retrieve orders by restaurant id when status is null")
    void shouldRetrieveOrdersByRestaurantIdWhenStatusIsNull() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final OrderEntity order1 = OrderEntity.builder().id(UUID.randomUUID()).restaurantId(restaurantId).build();
        final OrderEntity order2 = OrderEntity.builder().id(UUID.randomUUID()).restaurantId(restaurantId).build();
        when(this.mockOrderGateway.findAllByRestaurantId(restaurantId)).thenReturn(Flux.just(order1, order2));

        // When
        final Flux<OrderEntity> result = this.useCase.execute(restaurantId, null);

        // Then
        StepVerifier.create(result).expectNext(order1).expectNext(order2).verifyComplete();
    }

    @Test
    @DisplayName("Should return empty flux when no orders found and status is null")
    void shouldReturnEmptyFluxWhenNoOrdersFoundAndStatusIsNull() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        when(this.mockOrderGateway.findAllByRestaurantId(restaurantId)).thenReturn(Flux.empty());

        // When
        final Flux<OrderEntity> result = this.useCase.execute(restaurantId, null);

        // Then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    @DisplayName("Should retrieve orders by restaurant id and status")
    void shouldRetrieveOrdersByRestaurantIdAndStatus() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final OrderStatus status = OrderStatus.PENDING;
        final OrderEntity order1 = OrderEntity.builder().id(UUID.randomUUID()).restaurantId(restaurantId).status(status).build();
        final OrderEntity order2 = OrderEntity.builder().id(UUID.randomUUID()).restaurantId(restaurantId).status(status).build();
        when(this.mockOrderGateway.findAllByRestaurantIdAndStatus(restaurantId, status)).thenReturn(Flux.just(order1, order2));

        // When
        final Flux<OrderEntity> result = this.useCase.execute(restaurantId, status);

        // Then
        StepVerifier.create(result).expectNext(order1).expectNext(order2).verifyComplete();
    }

    @Test
    @DisplayName("Should return empty flux when no orders found for a given status")
    void shouldReturnEmptyFluxWhenNoOrdersFoundForGivenStatus() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final OrderStatus status = OrderStatus.PENDING;
        when(this.mockOrderGateway.findAllByRestaurantIdAndStatus(restaurantId, status)).thenReturn(Flux.empty());

        // When
        final Flux<OrderEntity> result = this.useCase.execute(restaurantId, status);

        // Then
        StepVerifier.create(result).verifyComplete();
    }
}
