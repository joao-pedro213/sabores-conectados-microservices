package com.postech.core.order.datasource;

import com.postech.core.order.dto.OrderDto;
import reactor.core.publisher.Mono;

public interface IOrderMessageProducer {
    Mono<Void> sendMessage(OrderDto orderDto);
}
