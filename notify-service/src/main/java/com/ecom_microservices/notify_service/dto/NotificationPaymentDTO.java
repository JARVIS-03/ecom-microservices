package com.ecom_microservices.notify_service.dto;

public class NotificationPaymentDTO {
    private String userEmail;
    private Long paymentId;
    private String status;
    public NotificationPaymentDTO(String userEmail, Long paymentId, String status) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
