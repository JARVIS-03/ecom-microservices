package com.ecom_microservices.notify_service.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.repository.NotificationRepository;
import com.ecom_microservices.notify_service.service.NotificationService;

@Component
public class NotificationProcessor {
	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
	
    @Autowired
    private NotificationRepository repository;

    @Autowired
    private EmailSender emailSender;

    @Scheduled(fixedDelay = 10000)
    public void processPendingNotifications() {
    	Pageable page = PageRequest.of(0, 10);
        List<Notification> pending = repository.findNotificationByStatusOrderByPriority(NotificationStatus.PENDING, page);
        logger.info("Notification Processor Started at " + System.currentTimeMillis() + ": Total pending notifications are "+pending.size());
        for (Notification notification : pending) {
            try {
                switch (notification.getType()) {
                    case EMAIL:
                        emailSender.send(notification);  
                        break;
                }
            } catch (Exception e) {
                logger.error("Error Occured while processing the notification with id: "+notification.getId());
            }
        }
    }
	
	
}
