package com.ecom_microservices.notify_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaRequest {
	
	private long id;
    
    private String userEmail;
    
	private String status;
	
	private String  serviceName;
}
