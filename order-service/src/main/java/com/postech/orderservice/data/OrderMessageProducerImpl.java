package com.postech.orderservice.data;

import com.postech.core.order.datasource.IOrderMessageProducer;
import com.postech.core.order.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class OrderMessageProducerImpl implements IOrderMessageProducer {
    private final ReactiveKafkaProducerTemplate<String, OrderDto> producerTemplate;
    private final String topicName;

    public OrderMessageProducerImpl(
            ReactiveKafkaProducerTemplate<String, OrderDto> producerTemplate,
            @Value("${kafka.topic.order}") String topicName) {
        this.producerTemplate = producerTemplate;
        this.topicName = topicName;
    }

    @Override
    public Mono<Void> sendMessage(OrderDto orderDto) {
        return this.producerTemplate
                .send(topicName, orderDto.getId().toString(), orderDto)
                .doOnSuccess(result ->
                        log.info(
                                "Successfully sent order message with id: {} to partition {} with offset {}",
                                orderDto.getId(),
                                result.recordMetadata().partition(),
                                result.recordMetadata().offset()))
                .doOnError(error ->
                        log.error(
                                "Failed to send order message with id: {}. Error: {}",
                                orderDto.getId(),
                                error.getMessage()))
                .then();
    }
}
