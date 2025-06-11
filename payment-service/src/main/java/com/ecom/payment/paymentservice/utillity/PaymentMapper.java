package com.ecom.payment.paymentservice.utillity;

import com.ecom.payment.paymentservice.dto.PaymentMethodDetails;
import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.model.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.ecom.payment.paymentservice.enums.PaymentStatus;

import java.time.LocalDateTime;

@Component
public class PaymentMapper {

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Payment toEntity(PaymentRequestDTO dto) {
        Payment payment = modelMapper.map(dto, Payment.class);
        payment.setPaymentId(null);
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setDate(LocalDateTime.now());
        System.out.println(serializeMethodDetails(dto.getMethodDetails()));
        payment.setPaymentDetails(serializeMethodDetails(dto.getMethodDetails()));
        return payment;
    }
//
//    public PaymentResponseDTO toDto(Payment payment) {
//        return modelMapper.map(payment, PaymentResponseDTO.class);
//    }
public PaymentResponseDTO toDto(Payment payment) {
    PaymentResponseDTO dto = modelMapper.map(payment, PaymentResponseDTO.class);

    // Deserialize paymentDetails JSON string into polymorphic PaymentMethodDetails object
    try {
        PaymentMethodDetails details = objectMapper.readValue(
                payment.getPaymentDetails(),
                PaymentMethodDetails.class
        );
        dto.setMethodDetails(details);
    } catch (Exception e) {
        throw new RuntimeException("Failed to deserialize paymentDetails", e);
    }

    return dto;
}

//    private String serializeMethodDetails(PaymentMethodDetails details) {
//        // Convert the polymorphic object to JSON string
//        try {
//            return new ObjectMapper().writeValueAsString(details);
//        } catch (Exception e) {
//            throw new RuntimeException("Serialization error", e);
//        }
//    }
private String serializeMethodDetails(PaymentMethodDetails details) {
    // Convert the polymorphic object to JSON string
    try {
        String json = new ObjectMapper().writeValueAsString(details);
        System.out.println("Serialized PaymentMethodDetails: " + json); // ✅ Print JSON
        return json;
    } catch (Exception e) {
        throw new RuntimeException("Serialization error", e);
    }
}

}
