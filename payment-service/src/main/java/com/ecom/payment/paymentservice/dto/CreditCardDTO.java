package com.ecom.payment.paymentservice.dto;

public class CreditCardDTO implements PaymentMethodDetails {
    private String type;
    private String cardNumber;
    private String expiry;
    private String cvv;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

}
