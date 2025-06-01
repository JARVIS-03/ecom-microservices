package com.ecom_microservices.payment_service.model.request;


import com.ecom_microservices.payment_service.model.constants.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PaymentRequest {
    @NotBlank
    private String orderId;

    @DecimalMin(value = "0.1")
    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;
}
