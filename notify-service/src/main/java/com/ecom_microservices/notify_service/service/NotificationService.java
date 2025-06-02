package com.ecom_microservices.notify_service.service;

import com.ecom_microservices.notify_service.dto.NotificationRequestDTO;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
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
