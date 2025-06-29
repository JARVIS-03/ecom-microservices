package com.ecom_microservices.notify_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.dto.NotificationOrderDTO;
import com.ecom_microservices.notify_service.dto.NotificationPaymentDTO;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "${kafka.request.order.topic}", groupId = "group-id", containerFactory = "orderKafkaListenerContainerFactory")
    public void consumeOrder(NotificationOrderDTO request) {
        NotificationResponseDTO response = notificationService.createOrderStatusNotification(request);
        logger.info("Kafka Response prepared: " + response);
    }

    @KafkaListener(topics = "${kafka.request.payment.topic}", groupId = "group-id", containerFactory = "paymentKafkaListenerContainerFactory")
    private void consumePayment(NotificationPaymentDTO request) {
        NotificationResponseDTO response = notificationService.createPaymentStatusNotification(request);
        logger.info("Kafka Response prepared: "+ response);
    }
}