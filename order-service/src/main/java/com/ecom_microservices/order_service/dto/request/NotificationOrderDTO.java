package com.ecom_microservices.order_service.dto.request;


import com.ecom_microservices.order_service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationOrderDTO {
    private long orderId;
    private String userEmail;
    private OrderStatus status;
}
