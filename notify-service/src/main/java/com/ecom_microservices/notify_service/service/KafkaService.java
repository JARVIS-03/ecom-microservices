
package com.ecom_microservices.notify_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecom_microservices.notify_service.dto.NotificationPaymentDTO;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.dto.OrderDTO;
import com.ecom_microservices.notify_service.dto.PaymentDTO;
import com.ecom_microservices.notify_service.enums.PaymentStatus;

@Service
public class KafkaService {
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private KafkaTemplate<String, NotificationResponseDTO> kafkaTemplate;
	
	@Value("${kafka.response.payment.topic}")
	private String PAYMENT_RESPONSE_TOPIC;
	
	@Value("${kafka.response.order.topic}")
	private String ORDER_RESPONSE_TOPIC;
	
	@KafkaListener(topics = "${kafka.request.payment.topic}")
    public void consumePayment(NotificationPaymentDTO request) {
		logger.info("Message Received from Kafka: "+ request);
		NotificationResponseDTO response = null;
		PaymentDTO paymentDTO = validatePaymentDto(request);
		notificationService.createPaymentStatusNotification(paymentDTO);
        logger.debug("Mesage sent to kafka : "+response);
    }

	@KafkaListener(topics = "${kafka.request.order.topic}")
    public void consumePayment(OrderDTO request) {
		logger.info("Message Received from Kafka: "+ request);
		NotificationResponseDTO response = notificationService.createOrderStatusNotification(request);
        logger.debug("Mesage sent to kafka :" + response);
    }
	
    private PaymentDTO validatePaymentDto(NotificationPaymentDTO notificationPaymentDTO){
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentId(notificationPaymentDTO.getPaymentId());
		paymentDTO.setStatus(PaymentStatus.valueOf(notificationPaymentDTO.getStatus()));
		paymentDTO.setUserEmail(notificationPaymentDTO.getUserEmail());
		return paymentDTO;
    }

}