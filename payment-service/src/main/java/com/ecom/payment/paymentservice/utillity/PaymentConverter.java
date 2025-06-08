package com.ecom.payment.paymentservice.utillity;


import com.ecom.payment.paymentservice.dto.*;
import com.ecom.payment.paymentservice.model.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class PaymentConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentResponseDTO toDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setOrderId(payment.getOrderId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus().name());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setDate(payment.getDate());

        try {
            Class<?> clazz = switch (payment.getPaymentMethod()) {
                case "DEBIT_CARD" -> DebitCardDTO.class;
                case "CREDIT_CARD" -> CreditCardDTO.class;
                case "PAYPAL" -> PaypalDTO.class;
                case "MONEY_TRANSFER" -> MoneyTransferDTO.class;
                default -> null;
            };
            if (clazz != null) {
                dto.setMethodDetails((PaymentMethodDetails) objectMapper.readValue(payment.getPaymentDetails(), clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
}
