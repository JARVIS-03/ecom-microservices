
package com.ecom_microservices.notify_service.dto;

import lombok.Data;
import com.ecom_microservices.notify_service.model.Notification;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long notificationId;
    private String recipient;
    private String messageContent;
    private Notification.NotificationType notificationType;
    private Notification.Priority priority;
    private Notification.Status status;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;


}

