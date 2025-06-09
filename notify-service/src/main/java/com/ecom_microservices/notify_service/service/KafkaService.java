package com.ecom_microservices.notify_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecom_microservices.notify_service.dto.KafkaRequest;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.dto.OrderDTO;
import com.ecom_microservices.notify_service.dto.PaymentDTO;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.enums.OrderStatus;
import com.ecom_microservices.notify_service.enums.PaymentStatus;

@Service
public class KafkaService {
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private KafkaTemplate<String, NotificationResponseDTO> kafkaTemplate;
	
	@Value("${kafka.response.topic}")
	private String RESPONSE_TOPIC;
	
	@KafkaListener(topics = "${kafka.request.topic}" , groupId = "${spring.kafka.consumer.group-id}")
    public void consume(KafkaRequest request) {
		logger.info("Message Received from Kafka: "+ request);
		NotificationResponseDTO response = null;
		if(request.getServiceName().contentEquals("payment")) {
			try {
				response = validatePaymentRequest(request);
			} catch (Exception e) {
				logger.error("Error occurred while processing the request");
			}
        } else if(request.getServiceName().contentEquals("order")) {
        	try {
        		response = validateOrderRequest(request);
        
        	} catch (Exception e) {
        		logger.error("Error occurred while processing the request");
			}
        }
		if(response == null){
        	response = new NotificationResponseDTO();
        	response.setId(request.getId());
        	response.setMessageContent("Bad Request.");
        	response.setStatus(NotificationStatus.FAILED.toString());
        	logger.error("Invalid request caught in Kafka : "+ response);
        }
        kafkaTemplate.send(RESPONSE_TOPIC, request.getServiceName(), response);
        logger.debug("Mesage sent to kafka : "+response);
    }
	
	private NotificationResponseDTO validatePaymentRequest(KafkaRequest request) {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setPaymentId(request.getId());
		paymentDTO.setUserEmail(request.getUserEmail());
		paymentDTO.setStatus(PaymentStatus.valueOf(request.getStatus()));
		
		return notificationService.createPaymentStatusNotification(paymentDTO);
		
	}
	
	private NotificationResponseDTO validateOrderRequest(KafkaRequest request) {
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(request.getId());
		orderDTO.setStatus(OrderStatus.valueOf(request.getStatus()));
		orderDTO.setUserEmail(request.getUserEmail());
		
		return notificationService.createOrderStatusNotification(orderDTO);
	}
	
}
