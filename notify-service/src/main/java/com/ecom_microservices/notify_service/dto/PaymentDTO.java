package com.ecom_microservices.notify_service.dto;

import com.ecom_microservices.notify_service.enums.PaymentStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

        private long paymentId;
        @Email
        @NotBlank(message = "user email can not be blank")
        private String userEmail;
        private PaymentStatus status;
    }