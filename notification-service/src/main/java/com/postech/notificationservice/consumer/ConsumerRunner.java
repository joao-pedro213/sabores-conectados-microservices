package com.postech.notificationservice.consumer;

import com.postech.core.order.dto.OrderDto;
import com.postech.core.reservation.dto.ReservationDto;
import com.postech.notificationservice.NotificationBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ConsumerRunner implements CommandLineRunner {
    private final ReactiveKafkaConsumerTemplate<String, Object> template;
    private final NotificationBuilder builder;

//    @Override
//    public void run(String... args) throws Exception {
//        this.template
//                .receive()
//                .doOnNext(message -> log.info(this.builder.build(message.value())))
//                .subscribe();
//    }

    @Override
    public void run(String... args) throws Exception {
        this.template
                .receive()
                .doOnNext(message -> {
                    Object payload = message.value();
                    if (payload instanceof OrderDto orderDto) {
                        String notification = this.builder.build(orderDto);
                        log.info("Order Notification: {}", notification);

                    } else if (payload instanceof ReservationDto reservationDto) {
                        String notification = this.builder.build(reservationDto);
                        log.info("Reservation Notification: {}", notification);

                    } else {
                        log.warn("Received message of unknown type: {}", payload.getClass().getName());
                    }
//                    message.receiverOffset().acknowledge();
                })
                .subscribe(
                        null,
                        error -> log.error("Kafka stream error: {}", error.getMessage()),
                        () -> log.info("Kafka consumer finished"));
    }
}
