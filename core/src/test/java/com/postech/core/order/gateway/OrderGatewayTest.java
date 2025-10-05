package com.postech.core.order.gateway;

import com.postech.core.helpers.OrderObjectMother;
import com.postech.core.order.datasource.IOrderDataSource;
import com.postech.core.order.datasource.IOrderMessageProducer;
import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.dto.OrderDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderGatewayTest {

    @Mock
    private IOrderDataSource dataSource;

    @Mock
    private IOrderMessageProducer messageProducer;

    @InjectMocks
    private OrderGateway gateway;

    private static Map<String, Object> getSampleOrderData() {
        return Map.of(
                "id", UUID.randomUUID(),
                "restaurantId", UUID.randomUUID(),
                "customerId", UUID.randomUUID(),
                "status", "PENDING",
                "items", List.of(Map.of("id", UUID.randomUUID(), "price", 10.0, "quantity", 1)),
                "createdAt", LocalDateTime.parse("2025-09-17T00:00:00.000")
        );
    }

    @Test
    void shouldSaveOrder() {
        // Given
        final Map<String, Object> orderSampleData = getSampleOrderData();
        final OrderEntity orderToSave = OrderObjectMother.buildOrderEntity(orderSampleData);
        final OrderDto savedOrderDto = OrderObjectMother.buildOrderDto(orderSampleData);
        when(this.dataSource.save(any(OrderDto.class))).thenReturn(Mono.just(savedOrderDto));

        // When
        final Mono<OrderEntity> result = this.gateway.save(orderToSave);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(savedOrder -> {
                    final ArgumentCaptor<OrderDto> argument = ArgumentCaptor.forClass(OrderDto.class);
                    verify(this.dataSource).save(argument.capture());
                    final OrderDto capturedOrderDto = argument.getValue();
                    final OrderDto expectedOrderDto = OrderObjectMother.buildOrderDto(orderSampleData);
                    assertThat(capturedOrderDto).usingRecursiveComparison().isEqualTo(expectedOrderDto);
                    assertThat(savedOrder).isNotNull();
                    final OrderEntity expectedUpdatedOrder = orderToSave.toBuilder().build();
                    assertThat(savedOrder).usingRecursiveComparison().isEqualTo(expectedUpdatedOrder);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldFindOrderById() {
        // Given
        final Map<String, Object> orderSampleData = getSampleOrderData();
        final OrderDto foundOrderDto = OrderObjectMother.buildOrderDto(orderSampleData);
        final UUID id = UUID.fromString(orderSampleData.get("id").toString());
        when(this.dataSource.findById(id)).thenReturn(Mono.just(foundOrderDto));

        // When
        final Mono<OrderEntity> result = this.gateway.findById(id);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(foundOrder -> {
                    final OrderEntity expectedFoundOrder = OrderObjectMother.buildOrderEntity(orderSampleData);
                    assertThat(foundOrder).usingRecursiveComparison().isEqualTo(expectedFoundOrder);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldFindAllByRestaurantId() {
        // Given
        final Map<String, Object> orderSampleData = getSampleOrderData();
        final List<OrderDto> foundOrderDtos = List.of(OrderObjectMother.buildOrderDto(orderSampleData));
        final UUID restaurantId = UUID.fromString(orderSampleData.get("restaurantId").toString());
        when(this.dataSource.findAllByRestaurantId(restaurantId)).thenReturn(Flux.fromIterable(foundOrderDtos));

        // When
        final Flux<OrderEntity> result = this.gateway.findAllByRestaurantId(restaurantId);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(foundOrder -> {
                    final OrderEntity expectedFoundOrder = OrderObjectMother.buildOrderEntity(orderSampleData);
                    assertThat(foundOrder).usingRecursiveComparison().isEqualTo(expectedFoundOrder);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldFindAllByRestaurantIdAndStatus() {
        // Given
        final Map<String, Object> orderSampleData = getSampleOrderData();
        final List<OrderDto> foundOrderDtos = List.of(OrderObjectMother.buildOrderDto(orderSampleData));
        final UUID restaurantId = UUID.fromString(orderSampleData.get("restaurantId").toString());
        final OrderStatus status = OrderStatus.valueOf((String) orderSampleData.get("status"));
        when(this.dataSource.findAllByRestaurantIdAndStatus(restaurantId, status)).thenReturn(Flux.fromIterable(foundOrderDtos));

        // When
        final Flux<OrderEntity> result = this.gateway.findAllByRestaurantIdAndStatus(restaurantId, status);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(foundOrder -> {
                    final OrderEntity expectedFoundOrder = OrderObjectMother.buildOrderEntity(orderSampleData);
                    assertThat(foundOrder).usingRecursiveComparison().isEqualTo(expectedFoundOrder);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldSendEvent() {
        // Given
        final Map<String, Object> orderSampleData = getSampleOrderData();
        final OrderEntity orderEntity = OrderObjectMother.buildOrderEntity(orderSampleData);
        when(this.messageProducer.sendMessage(any(OrderDto.class))).thenReturn(Mono.empty());

        // When
        final Mono<Void> result = this.gateway.sendEvent(orderEntity);

        // Then
        StepVerifier.create(result).verifyComplete();
        final ArgumentCaptor<OrderDto> argument = ArgumentCaptor.forClass(OrderDto.class);
        verify(this.messageProducer).sendMessage(argument.capture());
        final OrderDto capturedOrderDto = argument.getValue();
        final OrderDto expectedOrderDto = OrderObjectMother.buildOrderDto(orderSampleData);
        assertThat(capturedOrderDto).usingRecursiveComparison().isEqualTo(expectedOrderDto);
    }
}
