package com.postech.reservationservice.api.config;

import com.postech.core.reservation.dto.ReservationDto;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

@Configuration
@EnableKafka
public class KafkaProducerConfig {
    @Bean
    public SenderOptions<String, ReservationDto> senderOptions(KafkaProperties kafkaProperties, SslBundles sslBundles) {
        return SenderOptions.<String, ReservationDto>create(kafkaProperties.buildProducerProperties(sslBundles));
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, ReservationDto> producerTemplate(SenderOptions<String, ReservationDto> options) {
        return new ReactiveKafkaProducerTemplate<>(options);
    }
}
