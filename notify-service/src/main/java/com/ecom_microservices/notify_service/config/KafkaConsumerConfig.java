package com.ecom_microservices.notify_service.config;

import com.ecom_microservices.notify_service.dto.NotificationOrderDTO;
import com.ecom_microservices.notify_service.dto.NotificationPaymentDTO;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class KafkaConsumerConfig {

    // ===== ORDER CONSUMER CONFIG =====
    @Bean
    public ConsumerFactory<String, NotificationOrderDTO> orderConsumerFactory() {
        JsonDeserializer<NotificationOrderDTO> deserializer = new JsonDeserializer<>(NotificationOrderDTO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationOrderDTO> orderKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationOrderDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderConsumerFactory());
        return factory;
    }

    // ===== PAYMENT CONSUMER CONFIG =====
    @Bean
    public ConsumerFactory<String, NotificationPaymentDTO> paymentConsumerFactory() {
        JsonDeserializer<NotificationPaymentDTO> deserializer = new JsonDeserializer<>(NotificationPaymentDTO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationPaymentDTO> paymentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationPaymentDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentConsumerFactory());
        return factory;
    }
}
