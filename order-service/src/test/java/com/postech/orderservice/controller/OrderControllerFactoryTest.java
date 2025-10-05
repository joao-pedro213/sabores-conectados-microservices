package com.postech.orderservice.controller;

import com.postech.core.order.controller.OrderController;
import com.postech.orderservice.data.OrderDataSourceImpl;
import com.postech.orderservice.data.OrderMessageProducerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class OrderControllerFactoryTest {

    @Mock
    private OrderDataSourceImpl orderDataSource;

    @Mock
    private OrderMessageProducerImpl orderMessageProducer;

    @InjectMocks
    private OrderControllerFactory orderControllerFactory;

    @Test
    void shouldBuildOrderControllerSuccessfully() {
        // Given
        OrderController expectedController = mock(OrderController.class);
        try (MockedStatic<OrderController> mockedStatic = Mockito.mockStatic(OrderController.class)) {
            mockedStatic.when(() -> OrderController.build(this.orderDataSource, this.orderMessageProducer)).thenReturn(expectedController);

            // When
            OrderController actualController = this.orderControllerFactory.build();

            // Then
            assertThat(actualController).isNotNull().isEqualTo(expectedController);
        }
    }
}
