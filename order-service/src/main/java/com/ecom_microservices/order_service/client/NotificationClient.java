package com.ecom_microservices.order_service.client;

import com.ecom_microservices.order_service.dto.request.OrderDTO;
import com.ecom_microservices.order_service.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    public final String NOTIFICATION_SERVICE_URL="http://NOTIFICATION-SERVICE/api/notifications/order/send";

    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;

    @Value("${kafka.request.order.topic}")
    private String topic;

    public void sendOrderNotification(OrderDTO notifyDTO) {
        kafkaTemplate.send(topic, notifyDTO);
        System.out.println("✅ Kafka message sent: " + notifyDTO);
    }

    public void sendNotification(Order savedOrder) {
        OrderDTO orderDTO = OrderDTO.builder()
                .orderId(savedOrder.getId())
                .userEmail("2002mohann@gmail.com")
                .status(savedOrder.getOrderStatus())
                .build();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<OrderDTO> request = new HttpEntity<>(orderDTO, headers);
            sendOrderNotification(orderDTO);
//            restTemplate.postForLocation(NOTIFICATION_SERVICE_URL, request);
            log.info("Notification sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }
}
