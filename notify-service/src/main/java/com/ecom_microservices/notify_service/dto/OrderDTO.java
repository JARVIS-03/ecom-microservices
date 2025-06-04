package com.ecom_microservices.notify_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ecom_microservices.notify_service.enums.OrderStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
	private long orderId;
    
	@Email
    @NotBlank(message = "user email can not be blank")
    private String userEmail;
    
	private OrderStatus status;
}