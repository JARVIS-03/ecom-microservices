package com.ecom_microservices.notify_service.service;

import com.ecom_microservices.notify_service.dto.NotificationRequestDTO;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public NotificationResponseDTO createNotification(NotificationRequestDTO requestDTO) {
        Notification notification = Notification.builder()
                .recipient(requestDTO.getRecipient())
                .messageContent(requestDTO.getMessageContent())
                .type(Enum.valueOf(com.ecom_microservices.notify_service.enums.NotificationType.class, requestDTO.getNotificationType().toUpperCase()))
                .priority(Enum.valueOf(com.ecom_microservices.notify_service.enums.PriorityLevel.class, requestDTO.getPriority().toUpperCase()))
                .status(NotificationStatus.PENDING)
                // .scheduledTime(requestDTO.getScheduledTime()) // if applicable
                .build();

        Notification saved = repository.save(notification);

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
        return repository.findByStatus(status);
    }

    public List<Notification> getNotificationsByRecipient(String recipient) {
        return repository.findByRecipient(recipient);
    }
    public List<Notification> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByCreatedTimestampBetween(startDate, endDate);
    }


}
