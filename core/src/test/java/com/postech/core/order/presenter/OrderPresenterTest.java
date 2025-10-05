package com.postech.core.order.presenter;

import com.postech.core.helpers.OrderObjectMother;
import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.dto.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPresenterTest {
    private OrderPresenter presenter;

    @BeforeEach
    void setUp() {
        this.presenter = OrderPresenter.build();
    }

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
    void shouldMapDomainToDto() {
        // Given
        final Map<String, Object> orderSampleData = getSampleOrderData();
        final OrderEntity orderEntity = OrderObjectMother.buildOrderEntity(orderSampleData);

        // When
        final OrderDto orderDto = this.presenter.toDto(orderEntity);

        // Then
        final OrderDto expectedOrderDto = OrderObjectMother.buildOrderDto(orderSampleData);
        assertThat(orderDto).usingRecursiveComparison().isEqualTo(expectedOrderDto);
    }
}
