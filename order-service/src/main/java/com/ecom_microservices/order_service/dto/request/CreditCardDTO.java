package com.ecom_microservices.order_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditCardDTO {
    private String type;
    private String cardNumber;
    private String expiry;
    private String cvv;
}
