package com.ecom_microservices.order_service.service;

import com.ecom_microservices.order_service.dto.request.OrderDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaService {
    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;

    @Value("${kafka.request.order.topic}")
    private String topic;

    public OrderKafkaService(KafkaTemplate<String, OrderDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderNotification(OrderDTO orderDTO) {
        kafkaTemplate.send(topic, orderDTO);
        System.out.println("===================Kafka message sent: " + orderDTO);
    }
}