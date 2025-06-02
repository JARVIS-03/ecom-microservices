package com.ecom_microservices.payment_service.model.request;


import com.ecom_microservices.payment_service.model.constants.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusUpdate {

    @NotNull(message = "Payment status is required")
    private PaymentStatus status;

}
