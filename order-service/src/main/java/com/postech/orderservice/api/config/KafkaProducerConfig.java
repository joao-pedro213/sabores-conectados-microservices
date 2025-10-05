package com.postech.orderservice.api.config;

import com.postech.core.order.dto.OrderDto;
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
    public SenderOptions<String, OrderDto> senderOptions(KafkaProperties kafkaProperties, SslBundles sslBundles) {
        return SenderOptions.<String, OrderDto>create(kafkaProperties.buildProducerProperties(sslBundles));
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, OrderDto> producerTemplate(SenderOptions<String, OrderDto> options) {
        return new ReactiveKafkaProducerTemplate<>(options);
    }
}
