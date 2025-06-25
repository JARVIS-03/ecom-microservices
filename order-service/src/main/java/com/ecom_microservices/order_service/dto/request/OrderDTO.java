package com.ecom_microservices.order_service.dto.request;


import com.ecom_microservices.order_service.enums.OrderStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private long orderId;

    @Email
    @NotBlank(message = "user email can not be blank")
    private String userEmail;

    private OrderStatus status;

}