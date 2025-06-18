package com.ecom.payment.paymentservice.dto;

import com.ecom.payment.paymentservice.enums.PaymentStatus;

public class NotificationDTO {
    private String userEmail;
    private Long paymentId;
    private PaymentStatus status;

    public NotificationDTO(String userEmail, Long paymentId, PaymentStatus status) {
        this.userEmail = userEmail;
        this.paymentId = paymentId;
        this.status = status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
