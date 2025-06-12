package com.ecom.payment.paymentservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class PaymentRequestDTO {
    @NotNull(message = "Order ID must not be null")
    private Long orderId;

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Payment method must not be blank")
    @Pattern(regexp = "CREDIT_CARD|DEBIT_CARD|PAYPAL|MONEY_TRANSFER", message = "Invalid payment method")
    private String paymentMethod;

    @NotNull(message = "Method details must not be null")
    private PaymentMethodDetails methodDetails;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    // Getters and setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethodDetails getMethodDetails() {
        return methodDetails;
    }

    public void setMethodDetails(PaymentMethodDetails methodDetails) {
        this.methodDetails = methodDetails;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
