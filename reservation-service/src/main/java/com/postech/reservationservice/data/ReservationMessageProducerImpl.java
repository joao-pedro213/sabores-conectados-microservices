package com.postech.reservationservice.data;

import com.postech.core.reservation.datasource.IReservationMessageProducer;
import com.postech.core.reservation.dto.ReservationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReservationMessageProducerImpl implements IReservationMessageProducer {
    private final ReactiveKafkaProducerTemplate<String, ReservationDto> producerTemplate;
    private final String topicName;

    public ReservationMessageProducerImpl(
            ReactiveKafkaProducerTemplate<String, ReservationDto> producerTemplate,
            @Value("${kafka.topic.reservation}") String topicName) {
        this.producerTemplate = producerTemplate;
        this.topicName = topicName;
    }

    @Override
    public Mono<Void> sendMessage(ReservationDto reservationDto) {
        return this.producerTemplate
                .send(topicName, reservationDto.getId().toString(), reservationDto)
                .doOnSuccess(result ->
                        log.info(
                                "Successfully sent reservation message with id: {} to partition {} with offset {}",
                                reservationDto.getId(),
                                result.recordMetadata().partition(),
                                result.recordMetadata().offset()))
                .doOnError(error ->
                        log.error(
                                "Failed to send reservation message with id: {}. Error: {}",
                                reservationDto.getId(),
                                error.getMessage()))
                .then();
    }
}
