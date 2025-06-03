package com.ecom_microservices.notify_service.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ecom_microservices.notify_service.enums.OrderStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private long orderId;
    private String userEmail;
    private OrderStatus status;
}