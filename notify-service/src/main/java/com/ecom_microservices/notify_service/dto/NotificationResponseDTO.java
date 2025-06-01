package com.ecom_microservices.notify_service.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Long id;
    private String recipient;
    private String messageContent;
    private String notificationType;
    private String priority;
    private String status;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
