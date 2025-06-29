package com.ecom_microservices.notify_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ecom_microservices.notify_service.enums.OrderStatus;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationOrderDTO {
	private long orderId;
    private String userEmail;
	private OrderStatus status;
}