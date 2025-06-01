
package com.ecom_microservices.notify_service.dto;

import lombok.Data;
import com.ecom_microservices.notify_service.enums.NotificationType;
import com.ecom_microservices.notify_service.enums.PriorityLevel;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long notificationId;
    private String recipient;
    private String messageContent;
    private NotificationType notificationType;
    private PriorityLevel priority;
    private NotificationStatus status;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;


}

