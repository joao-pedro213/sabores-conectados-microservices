package com.postech.core.order.controller;

import com.postech.core.helpers.OrderObjectMother;
import com.postech.core.order.datasource.IOrderDataSource;
import com.postech.core.order.datasource.IOrderMessageProducer;
import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.domain.usecase.ChangeOrderStatusUseCase;
import com.postech.core.order.domain.usecase.CreateOrderUseCase;
import com.postech.core.order.domain.usecase.RetrieveOrdersByRestaurantIdUseCase;
import com.postech.core.order.dto.NewOrderDto;
import com.postech.core.order.dto.NewOrderItemDto;
import com.postech.core.order.dto.OrderDto;
import com.postech.core.order.gateway.OrderGateway;
import com.postech.core.order.presenter.OrderPresenter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private IOrderDataSource mockOrderDataSource;

    @Mock
    private IOrderMessageProducer mockOrderMessageProducer;

    @InjectMocks
    private OrderController controller;

    @Mock
    private OrderPresenter mockOrderPresenter;

    private MockedStatic<OrderPresenter> mockedStaticOrderPresenter;

    @BeforeEach
    void setUp() {
        this.mockedStaticOrderPresenter = mockStatic(OrderPresenter.class);
        this.mockedStaticOrderPresenter.when(OrderPresenter::build).thenReturn(this.mockOrderPresenter);
    }

    private static Map<String, Object> getSampleOrderData() {
        return Map.of(
                "id", UUID.randomUUID(),
                "restaurantId", UUID.randomUUID(),
                "customerId", UUID.randomUUID(),
                "status", OrderStatus.PENDING.toString(),
                "items", List.of(Map.of("id", UUID.randomUUID(), "price", 10.0, "quantity", 1)),
                "createdAt", LocalDateTime.parse("2025-09-17T00:00:00.000")
        );
    }

    @Test
    void shouldCreateOrder() {
        // Given
        final Map<String, Object> orderSampleData = getSampleOrderData();
        final NewOrderDto newOrderDto = NewOrderDto
                .builder()
                .restaurantId((UUID) orderSampleData.get("restaurantId"))
                .customerId((UUID) orderSampleData.get("customerId"))
                .items(List.of(NewOrderItemDto.builder().build()))
                .build();
        final OrderEntity createdOrderEntity = OrderObjectMother.buildOrderEntity(orderSampleData);
        final OrderDto createdOrderDto = OrderDto.builder().build();
        final CreateOrderUseCase mockCreateOrderUseCase = mock(CreateOrderUseCase.class);
        when(mockCreateOrderUseCase.execute(any(OrderEntity.class))).thenReturn(Mono.just(createdOrderEntity));
        when(this.mockOrderPresenter.toDto(createdOrderEntity)).thenReturn(createdOrderDto);
        try (MockedStatic<CreateOrderUseCase> mockedStaticCreateOrderUseCase = mockStatic(CreateOrderUseCase.class)) {
            mockedStaticCreateOrderUseCase.when(() -> CreateOrderUseCase.build(any(OrderGateway.class))).thenReturn(mockCreateOrderUseCase);

            // When
            final Mono<OrderDto> result = this.controller.createOrder(newOrderDto);

            // Then
            StepVerifier
                    .create(result)
                    .expectNextMatches(orderDto -> {
                        assertThat(orderDto).isNotNull().isEqualTo(createdOrderDto);
                        return true;
                    })
                    .verifyComplete();
        }
    }

    @Test
    void shouldRetrieveOrdersByRestaurantId() {
        // Given
        final Map<String, Object> orderSampleData = getSampleOrderData();
        final UUID restaurantId = (UUID) orderSampleData.get("restaurantId");
        final OrderStatus status = OrderStatus.valueOf((String) orderSampleData.get("status"));
        final List<OrderEntity> foundOrders = List.of(OrderObjectMother.buildOrderEntity(orderSampleData));
        final RetrieveOrdersByRestaurantIdUseCase mockRetrieveOrdersUseCase = mock(RetrieveOrdersByRestaurantIdUseCase.class);
        when(mockRetrieveOrdersUseCase.execute(restaurantId, status)).thenReturn(Flux.fromIterable(foundOrders));
        final OrderDto foundOrderDto = OrderDto.builder().build();
        when(this.mockOrderPresenter.toDto(any(OrderEntity.class))).thenReturn(foundOrderDto);
        try (MockedStatic<RetrieveOrdersByRestaurantIdUseCase> mockedStaticRetrieveOrdersUseCase = mockStatic(RetrieveOrdersByRestaurantIdUseCase.class)) {
            mockedStaticRetrieveOrdersUseCase.when(() -> RetrieveOrdersByRestaurantIdUseCase.build(any(OrderGateway.class))).thenReturn(mockRetrieveOrdersUseCase);

            // When
            final Flux<OrderDto> result = this.controller.retrieveOrdersByRestaurantId(restaurantId, status);

            // Then
            StepVerifier
                    .create(result)
                    .expectNextMatches(orderDto -> {
                        assertThat(orderDto).isNotNull().isEqualTo(foundOrderDto);
                        return true;
                    })
                    .verifyComplete();
        }
    }

    @Test
    void shouldChangeOrderStatus() {
        // Given
        final UUID orderId = UUID.randomUUID();
        final OrderStatus newStatus = OrderStatus.ACCEPTED;
        final Map<String, Object> orderSampleData = getSampleOrderData();
        final OrderEntity updatedOrderEntity = OrderObjectMother.buildOrderEntity(orderSampleData);
        final ChangeOrderStatusUseCase mockChangeOrderStatusUseCase = mock(ChangeOrderStatusUseCase.class);
        when(mockChangeOrderStatusUseCase.execute(orderId, newStatus)).thenReturn(Mono.just(updatedOrderEntity));
        final OrderDto updatedOrderDto = OrderDto.builder().build();
        when(this.mockOrderPresenter.toDto(updatedOrderEntity)).thenReturn(updatedOrderDto);
        try (MockedStatic<ChangeOrderStatusUseCase> mockedStaticChangeOrderStatusUseCase = mockStatic(ChangeOrderStatusUseCase.class)) {
            mockedStaticChangeOrderStatusUseCase.when(() -> ChangeOrderStatusUseCase.build(any(OrderGateway.class))).thenReturn(mockChangeOrderStatusUseCase);

            // When
            final Mono<OrderDto> result = this.controller.changeOrderStatus(orderId, newStatus);

            // Then
            StepVerifier
                    .create(result)
                    .expectNextMatches(orderDto -> {
                        assertThat(orderDto).isNotNull().isEqualTo(updatedOrderDto);
                        return true;
                    })
                    .verifyComplete();
        }
    }

    @AfterEach
    void tearDown() {
        if (this.mockedStaticOrderPresenter != null) {
            this.mockedStaticOrderPresenter.close();
        }
    }
}
