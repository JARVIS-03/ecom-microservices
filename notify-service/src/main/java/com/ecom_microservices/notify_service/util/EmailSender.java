package com.ecom_microservices.notify_service.util;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.repository.NotificationRepository;
import com.ecom_microservices.notify_service.service.NotificationService;

import jakarta.mail.SendFailedException;

@Component
public class EmailSender {
	
	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Retryable(
			maxAttempts = 3,
			backoff = @Backoff(delay = 2000))
	public void send(Notification notification) {
		logger.info("Trying to send notification with id: "+notification.getId());
		try {
			SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(notification.getRecipient());
	        message.setSubject("Notification System Status Update"); //Need to change
	        message.setText(notification.getMessageContent());
	        mailSender.send(message);
	            
	        logger.info("Notification(Email) sent successfully for id: "+notification.getId());
	        notification.setStatus(NotificationStatus.SENT);
//	        notification.setUpdatedTimestamp(LocalDateTime.now());
	        notificationRepository.save(notification);
		 }
		 catch (Exception ex) {
	            logger.warn("Attempt to send email for id: "+notification.getId()+" FAILED");
	            notification.setStatus(NotificationStatus.RETRIED);
	            notificationRepository.save(notification);
	            throw ex;
	        }
	    }
	
	 @Recover
	 public void recover(Exception ex, Notification notification) {
	        logger.warn("Failed to send after retries. Marking as FAILED for id: " + notification.getId());
	        notification.setStatus(NotificationStatus.FAILED);
	        notificationRepository.save(notification);
	 }
}