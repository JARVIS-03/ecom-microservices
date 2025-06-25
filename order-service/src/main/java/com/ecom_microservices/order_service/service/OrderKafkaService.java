package com.ecom_microservices.order_service.service;

import com.ecom_microservices.order_service.dto.request.NotificationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaService {
    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    @Value("${kafka.request.order.topic}")
    private String topic;

    public OrderKafkaService(KafkaTemplate<String, NotificationRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderNotification(NotificationRequest orderDTO) {
        kafkaTemplate.send(topic, orderDTO);
        System.out.println("✅ Kafka message sent: " + orderDTO);
    }
}