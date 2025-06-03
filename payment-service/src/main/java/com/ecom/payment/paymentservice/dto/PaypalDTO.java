package com.ecom.payment.paymentservice.dto;

public class PaypalDTO implements PaymentMethodDetails {
    private String type;
    private String paypalEmail;

    public String getPaypalEmail() {
        return paypalEmail;
    }

    public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

}
