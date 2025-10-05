package com.postech.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;

@Configuration
public class ConsumerConfig {
    @Bean
    public ReceiverOptions<String, Object> receiverOptions(
            KafkaProperties kafkaProperties,
            SslBundles sslBundles,
            @Value("${kafka.topics}") List<String> topics) {
        return ReceiverOptions
                .<String, Object>create(kafkaProperties.buildConsumerProperties(sslBundles))
                .consumerProperty(JsonDeserializer.USE_TYPE_INFO_HEADERS, true)
                .subscription(topics);
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Object> consumerTemplate(ReceiverOptions<String, Object> options) {
        return new ReactiveKafkaConsumerTemplate<>(options);
    }
}
