package com.ecom_microservices.order_service.client;

import com.ecom_microservices.order_service.dto.request.NotificationOrderDTO;
import com.ecom_microservices.order_service.entity.Order;
import com.ecom_microservices.order_service.enums.OrderStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationClient {

    // private final RestTemplate restTemplate;

    public final String NOTIFICATION_SERVICE_URL="http://NOTIFICATION-SERVICE/api/notifications/order/send";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${order.topic.name}")
    private String topic;

    public void sendOrderNotification(NotificationOrderDTO notificationOrderDTO) {
        kafkaTemplate.send(topic, notificationOrderDTO);
        log.info("Notification event sent from the Order Service to Kafka.");
    }

    public void sendNotification(Order savedOrder) {
        // NotificationOrderDTO orderDTO = NotificationOrderDTO.builder()
        //         .orderId(savedOrder.getId())
        //         .userEmail("2002mohann@gmail.com")
        //         .status(savedOrder.getOrderStatus())
        //         .build();

            NotificationOrderDTO notificationOrderDTO = NotificationOrderDTO.builder()
                .orderId(savedOrder.getId())
                .userEmail("mukeshmukey6353@gmail.com")
                .status(OrderStatus.DELIVERED)
                .build();
        try {
            // HttpHeaders headers = new HttpHeaders();
            // headers.setContentType(MediaType.APPLICATION_JSON);
            // HttpEntity<NotificationOrderDTO> request = new HttpEntity<>(orderDTO, headers);
            sendOrderNotification(notificationOrderDTO);
//            restTemplate.postForLocation(NOTIFICATION_SERVICE_URL, request);
            log.info("Notification sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }
}
