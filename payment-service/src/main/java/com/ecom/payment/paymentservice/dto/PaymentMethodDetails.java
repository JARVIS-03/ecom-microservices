package com.ecom.payment.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",  // required field in the JSON body
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DebitCardDTO.class, name = "DEBIT_CARD"),
        @JsonSubTypes.Type(value = CreditCardDTO.class, name = "CREDIT_CARD"),
        @JsonSubTypes.Type(value = PaypalDTO.class, name = "PAYPAL"),
        @JsonSubTypes.Type(value = MoneyTransferDTO.class, name = "MONEY_TRANSFER")
})
public interface PaymentMethodDetails {
    String getType();
    void setType(String type);
}
