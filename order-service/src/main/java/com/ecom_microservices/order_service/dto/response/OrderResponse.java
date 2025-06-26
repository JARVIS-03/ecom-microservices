package com.ecom_microservices.order_service.dto.response;

import com.ecom_microservices.order_service.entity.OrderItem;
import com.ecom_microservices.order_service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private long id;
    private List<OrderItem> orderItems;
    private long customerIdentifier;
    private int totalQuantity;
    private long totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
}
