package com.ecom.payment.paymentservice.dto;

public class MoneyTransferDTO implements PaymentMethodDetails {
    private String type;
    private String bankAccountNumber;
    private String ifscCode;

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

}
