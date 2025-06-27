package com.ecom_microservices.notify_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecom_microservices.notify_service.dto.NotificationDTO;

import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.dto.OrderDTO;
import com.ecom_microservices.notify_service.dto.PaymentDTO;
import com.ecom_microservices.notify_service.enums.PaymentStatus;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private KafkaTemplate<String, NotificationResponseDTO> kafkaTemplate;


    // @KafkaListener(topics = "${kafka.request.payment.topic}",
    //                containerFactory = "paymentKafkaListenerContainerFactory")
    // public void consumePayment(NotificationDTO request) {
    //     logger.info("Message Received from Kafka [Payment]: {}", request);
    //     PaymentDTO paymentDTO = validatePaymentDto(request);
    //     notificationService.createPaymentStatusNotification(paymentDTO);
    // }

    @KafkaListener(topics = "${kafka.request.order.topic}",
                   containerFactory = "orderKafkaListenerContainerFactory")
    public void consumeOrder(OrderDTO request) {
        System.out.println("==============================================");
        logger.info("================Message Received from Kafka [Order]: {}", request);
        NotificationResponseDTO response = notificationService.createOrderStatusNotification(request);
        logger.debug("Response prepared: {}", response);
    }

    private PaymentDTO validatePaymentDto(NotificationDTO notificationPaymentDTO) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentId(notificationPaymentDTO.getPaymentId());
        paymentDTO.setStatus(PaymentStatus.valueOf(notificationPaymentDTO.getStatus()));
        paymentDTO.setUserEmail(notificationPaymentDTO.getUserEmail());
        return paymentDTO;
    }
}