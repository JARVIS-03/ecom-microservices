package com.ecom.payment.paymentservice.dto;

import com.ecom.payment.paymentservice.enums.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentResponseDTO {
    private Long paymentId;
    private Long orderId;
    private Double amount;
    private PaymentStatus status;
    private String paymentMethod;
    private LocalDateTime date;
    private PaymentMethodDetails methodDetails;
    private String email;

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PaymentMethodDetails getMethodDetails() {
        return methodDetails;
    }

    public void setMethodDetails(PaymentMethodDetails methodDetails) {
        this.methodDetails = methodDetails;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
