package com.postech.orderservice.controller;

import com.postech.core.order.controller.OrderController;
import com.postech.orderservice.data.OrderDataSourceImpl;
import com.postech.orderservice.data.OrderMessageProducerImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderControllerFactory {
    private final OrderDataSourceImpl dataSource;
    private final OrderMessageProducerImpl messageProducer;

    public OrderController build() {
        return OrderController.build(dataSource, messageProducer);
    }
}
