package com.ecom_microservices.notify_service.service;

import com.ecom_microservices.notify_service.dto.NotificationRequestDTO;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.dto.OrderDTO;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.enums.NotificationType;
import com.ecom_microservices.notify_service.enums.OrderStatus;
import com.ecom_microservices.notify_service.enums.PriorityLevel;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.repository.NotificationRepository;
import com.ecom_microservices.notify_service.util.EmailSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;
    
    private final EmailSender emailSender;

    public NotificationService(NotificationRepository repository, EmailSender emailSender) {
        this.repository = repository;
        this.emailSender = emailSender;
    }

    public NotificationResponseDTO createNotification(NotificationRequestDTO requestDTO) {
        logger.info("Creating notification for recipient: {}", requestDTO.getRecipient());
        Notification notification = Notification.builder()
                .recipient(requestDTO.getRecipient())
                .messageContent(requestDTO.getMessageContent())
                .type(Enum.valueOf(com.ecom_microservices.notify_service.enums.NotificationType.class, requestDTO.getNotificationType().toUpperCase()))
                .priority(Enum.valueOf(com.ecom_microservices.notify_service.enums.PriorityLevel.class, requestDTO.getPriority().toUpperCase()))
                .status(NotificationStatus.PENDING)
                // .scheduledTime(requestDTO.getScheduledTime()) // if applicable
                .build();

        Notification saved = repository.save(notification);
        logger.debug("Notification saved with ID: {}", saved.getId());
        return new NotificationResponseDTO(
                saved.getId(),
                saved.getRecipient(),
                saved.getMessageContent(),
                saved.getType().name(),
                saved.getPriority().name(),
                saved.getStatus().name(),
                saved.getCreatedTimestamp(),
                saved.getUpdatedTimestamp()
        );
    }
    
    public NotificationResponseDTO createNotification(OrderDTO orderDTO) {
        logger.info("Creating notification for recipient: {}", orderDTO.getUserEmail());

        OrderStatus status = orderDTO.getStatus(); // now this is an enum
        String message;

        switch (status) {
            case SHIPPED:
                message = "Order shipped successfully, please be available";
                break;
            case DELIVERED:
                message = "Order successfully delivered";
                break;
            default:
                message = "Order is in " + status.name() + " state";
                break;
        }

        Notification notification = Notification.builder()
                .recipient(orderDTO.getUserEmail())
                .messageContent(message)
                .type(NotificationType.EMAIL) // set properly
                .priority(status == OrderStatus.DELIVERED ? PriorityLevel.HIGH : PriorityLevel.LOW)
                .status(NotificationStatus.PENDING)
                .scheduledTime(status != OrderStatus.DELIVERED ? LocalDateTime.now().plusMinutes(5) : null)
                .build();

        Notification saved = repository.save(notification);
        logger.debug("Notification saved with ID: {}", saved.getId());

        if (saved.getPriority() == PriorityLevel.HIGH) {
            emailSender.send(saved);
        }

        return new NotificationResponseDTO(
                saved.getId(),
                saved.getRecipient(),
                saved.getMessageContent(),
                saved.getType().name(),
                saved.getPriority().name(),
                saved.getStatus().name(),
                saved.getCreatedTimestamp(),
                saved.getUpdatedTimestamp()
        );
    }


    public List<Notification> getNotificationsByStatus(NotificationStatus status) {
        logger.info("Fetching notifications with status: {}", status);
        List<Notification> notifications =  repository.findByStatus(status);
        logger.debug("Fetched {} notifications with status {}", notifications.size(), status);
        return notifications;
    }

    public List<Notification> getNotificationsByRecipient(String recipient) {
        logger.info("Fetching notifications for recipient: {}", recipient);
        List<Notification> notifications = repository.findByRecipient(recipient);
        logger.debug("Fetched {} notifications for recipient {}", notifications.size(), recipient);
        return notifications;
    }
    public List<Notification> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Fetching notifications between {} and {}", startDate, endDate);
        List<Notification> notifications =  repository.findByCreatedTimestampBetween(startDate, endDate);
        logger.debug("Fetched {} notifications in date range", notifications.size());
        return notifications;
    }


}
