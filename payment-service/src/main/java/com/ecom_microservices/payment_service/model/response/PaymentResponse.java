package com.ecom_microservices.payment_service.model.response;


import com.ecom_microservices.payment_service.model.constants.PaymentStatus;

public class PaymentResponse {
    private Long id;
    private String orderId;
    private PaymentStatus status;
}
