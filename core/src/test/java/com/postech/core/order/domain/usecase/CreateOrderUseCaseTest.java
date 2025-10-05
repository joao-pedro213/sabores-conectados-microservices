package com.postech.core.order.domain.usecase;

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
class CreateOrderUseCaseTest {

    @Mock
    private OrderGateway mockGateway;

    @InjectMocks
    private CreateOrderUseCase useCase;

    @Test
    @DisplayName("Should create a new Order")
    void shouldCreateOrder() {
        // Given
        final OrderEntity newOrderEntity = OrderEntity.builder().build();
        final OrderEntity createdOrderEntity = newOrderEntity.toBuilder().id(UUID.randomUUID()).build();
        when(this.mockGateway.save(any(OrderEntity.class))).thenReturn(Mono.just(createdOrderEntity));
        when(this.mockGateway.sendEvent(any(OrderEntity.class))).thenReturn(Mono.empty());

        // When
        final Mono<OrderEntity> result = this.useCase.execute(newOrderEntity);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(orderEntity -> orderEntity.equals(createdOrderEntity))
                .verifyComplete();
        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(this.mockGateway).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isNotNull().isEqualTo(OrderStatus.PENDING);
        verify(this.mockGateway).sendEvent(createdOrderEntity);
    }
}
