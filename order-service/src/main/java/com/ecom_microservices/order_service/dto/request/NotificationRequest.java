package com.ecom_microservices.order_service.dto.request;


import com.ecom_microservices.order_service.enums.OrderStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private long orderId;
    private String userEmail;
    private OrderStatus status;
}
