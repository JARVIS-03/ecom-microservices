package com.ecom_microservices.order_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    private long orderId;
    private long amount;
    private String paymentMethod;
}

