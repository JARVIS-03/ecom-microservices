package com.ecom_microservices.notify_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {

    @NotBlank(message = "Recipient is required")
    private String recipient;

    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message content must be at most 1000 characters")
    private String messageContent;

    @NotBlank(message = "Notification type is required")
    private String notificationType;

    @NotBlank(message = "Priority is required")
    private String priority;

    private String scheduledTime;
}
