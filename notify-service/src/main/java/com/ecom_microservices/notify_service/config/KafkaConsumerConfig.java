package com.ecom_microservices.notify_service.config;

import com.ecom_microservices.notify_service.dto.NotificationPaymentDTO;
import com.ecom_microservices.notify_service.dto.OrderDTO;
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

    // ====== ORDER DTO CONFIG ======
    @Bean
    public ConsumerFactory<String, OrderDTO> orderConsumerFactory() {
        JsonDeserializer<OrderDTO> deserializer = new JsonDeserializer<>(OrderDTO.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "notification-order-group");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderDTO> orderKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderConsumerFactory());
        return factory;
    }

    // ====== PAYMENT DTO CONFIG ======
    @Bean
    public ConsumerFactory<String, NotificationPaymentDTO> paymentConsumerFactory() {
        JsonDeserializer<NotificationPaymentDTO> deserializer = new JsonDeserializer<>(NotificationPaymentDTO.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "notification-payment-group");
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
