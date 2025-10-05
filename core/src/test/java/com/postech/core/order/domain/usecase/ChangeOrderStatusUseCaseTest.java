package com.postech.core.order.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.gateway.OrderGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeOrderStatusUseCaseTest {

    @Mock
    private OrderGateway mockGateway;

    @InjectMocks
    private ChangeOrderStatusUseCase useCase;

    @Test
    @DisplayName("Should change order status")
    void shouldChangeOrderStatus() {
        // Given
        final UUID id = UUID.randomUUID();
        final OrderStatus newStatus = OrderStatus.ACCEPTED;
        final OrderEntity existingOrder = OrderEntity.builder().id(id).status(OrderStatus.PENDING).build();
        final OrderEntity updatedOrder = existingOrder.toBuilder().status(newStatus).build();
        when(this.mockGateway.findById(id)).thenReturn(Mono.just(existingOrder));
        when(this.mockGateway.save(any(OrderEntity.class))).thenReturn(Mono.just(updatedOrder));
        when(this.mockGateway.sendEvent(any(OrderEntity.class))).thenReturn(Mono.empty());

        // When
        final Mono<OrderEntity> result = this.useCase.execute(id, newStatus);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(orderEntity -> orderEntity.equals(updatedOrder))
                .verifyComplete();
        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(this.mockGateway).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isNotNull().isEqualTo(newStatus);
        verify(this.mockGateway).sendEvent(updatedOrder);
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(this.mockGateway.findById(any(UUID.class))).thenReturn(Mono.empty());

        // When
        final Mono<OrderEntity> result = this.useCase.execute(UUID.randomUUID(), OrderStatus.CANCELLED);

        // Then
        StepVerifier.create(result).expectError(EntityNotFoundException.class).verify();
    }
}
