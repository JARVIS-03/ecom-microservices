package com.ecom.payment.paymentservice.dto;

import java.time.LocalDateTime;

public class PaymentResponseDTO {
    private Long paymentId;
    private String orderId;
    private Double amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime date;
    private PaymentMethodDetails methodDetails;

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public PaymentMethodDetails getMethodDetails() {
        return methodDetails;
    }

    public void setMethodDetails(PaymentMethodDetails methodDetails) {
        this.methodDetails = methodDetails;
    }
}
