package com.ecom_microservices.notify_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaResponse {
	
	private long id;
	
	private KafkaRequest request;
}
