package com.ecom.payment.paymentservice.service;

import com.ecom.payment.paymentservice.dto.NotificationPaymentDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
//@RequiredArgsConstructor
//@Slf4j
public class NotificationServiceClient {
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceClient.class);
//    @Autowired
//    private RestTemplate restTemplate;
//    private static final String NOTIFICATION_URL = "http://localhost:8081/api/notifications/payment/send";
//
//    @CircuitBreaker(name = "notificationService", fallbackMethod = "notificationFallback")
//    @Retry(name = "notificationService")
//    public void sendNotification(PaymentResponseDTO response, String email) {
//        NotificationDTO dto = new NotificationDTO(email, response.getPaymentId(), response.getStatus());
//        restTemplate.postForObject(NOTIFICATION_URL, dto, Void.class);
//        log.info("Notification sent to: {}", email);
//    }
//
//    public void notificationFallback(PaymentResponseDTO response, String email, Throwable t) {
//        log.error("Notification fallback triggered for email: {}", email, t);
//        throw new RuntimeException("NotificationService is down. Unable to notify " + email);
//    }

    private final KafkaTemplate<String, NotificationPaymentDTO> kafkaTemplate;
    private final String topicName;

    public NotificationServiceClient(KafkaTemplate<String, NotificationPaymentDTO> kafkaTemplate,
                                     @Value("${notification.topic.name}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendNotification(PaymentResponseDTO response) {
        NotificationPaymentDTO dto = new NotificationPaymentDTO(response.getEmail(), response.getPaymentId(), response.getStatus());
        kafkaTemplate.send(topicName, dto);
        log.info("Notification event sent to Kafka for user: {}", response.getEmail());
    }
}
