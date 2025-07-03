package com.ecom_microservices.notify_service.dto;


import com.ecom_microservices.notify_service.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPaymentDTO {
    private String userEmail;
    private Long paymentId;
    private PaymentStatus status;
    
}
