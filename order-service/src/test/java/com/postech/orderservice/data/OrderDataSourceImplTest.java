package com.postech.orderservice.data;

import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.dto.OrderDto;
import com.postech.orderservice.data.document.OrderDocument;
import com.postech.orderservice.data.repository.IOrderRepository;
import com.postech.orderservice.mapper.IOrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDataSourceImplTest {

    @Mock
    private IOrderRepository mockRepository;

    @Mock
    private IOrderMapper mockMapper;

    @InjectMocks
    private OrderDataSourceImpl dataSource;

    @Test
    void shouldSaveOrder() {
        // Given
        final OrderDto orderToSaveDto = OrderDto.builder().build();
        final OrderDocument orderToSave = OrderDocument.builder().build();
        final OrderDocument savedOrder = OrderDocument.builder().id(UUID.randomUUID()).build();
        final OrderDto expectedSavedOrderDto = OrderDto.builder().id(savedOrder.getId()).build();
        when(this.mockMapper.toOrderDocument(any(OrderDto.class))).thenReturn(orderToSave);
        when(this.mockRepository.save(orderToSave)).thenReturn(Mono.just(savedOrder));
        when(this.mockMapper.toOrderDto(savedOrder)).thenReturn(expectedSavedOrderDto);

        // When
        Mono<OrderDto> result = this.dataSource.save(orderToSaveDto);

        // Then
        StepVerifier.create(result).expectNext(expectedSavedOrderDto).verifyComplete();
    }

    @Test
    void shouldFindOrderById() {
        // Given
        final UUID id = UUID.randomUUID();
        final OrderDocument foundOrder = OrderDocument.builder().id(id).build();
        final OrderDto mappedOrderDto = OrderDto.builder().id(id).build();
        when(this.mockRepository.findById(id)).thenReturn(Mono.just(foundOrder));
        when(this.mockMapper.toOrderDto(foundOrder)).thenReturn(mappedOrderDto);

        // When
        Mono<OrderDto> result = this.dataSource.findById(id);

        // Then
        StepVerifier.create(result).expectNext(mappedOrderDto).verifyComplete();
    }

    @Test
    void shouldFindAllOrdersByRestaurantId() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final OrderDocument order1 = OrderDocument.builder().id(UUID.randomUUID()).build();
        final OrderDocument order2 = OrderDocument.builder().id(UUID.randomUUID()).build();
        final OrderDto dto1 = OrderDto.builder().id(order1.getId()).build();
        final OrderDto dto2 = OrderDto.builder().id(order2.getId()).build();
        when(this.mockRepository.findAllByRestaurantId(restaurantId)).thenReturn(Flux.just(order1, order2));
        when(this.mockMapper.toOrderDto(order1)).thenReturn(dto1);
        when(this.mockMapper.toOrderDto(order2)).thenReturn(dto2);

        // When
        Flux<OrderDto> result = this.dataSource.findAllByRestaurantId(restaurantId);

        // Then
        StepVerifier.create(result).expectNext(dto1, dto2).verifyComplete();
    }

    @Test
    void shouldFindAllOrdersByRestaurantIdAndStatus() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final OrderStatus status = OrderStatus.PENDING;
        final OrderDocument order1 = OrderDocument.builder().id(UUID.randomUUID()).build();
        final OrderDocument order2 = OrderDocument.builder().id(UUID.randomUUID()).build();
        final OrderDto dto1 = OrderDto.builder().id(order1.getId()).build();
        final OrderDto dto2 = OrderDto.builder().id(order2.getId()).build();
        when(this.mockRepository.findAllByRestaurantIdAndStatus(restaurantId, status)).thenReturn(Flux.just(order1, order2));
        when(this.mockMapper.toOrderDto(order1)).thenReturn(dto1);
        when(this.mockMapper.toOrderDto(order2)).thenReturn(dto2);

        // When
        Flux<OrderDto> result = this.dataSource.findAllByRestaurantIdAndStatus(restaurantId, status);

        // Then
        StepVerifier.create(result).expectNext(dto1, dto2).verifyComplete();
    }
}
