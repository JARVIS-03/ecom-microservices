package com.ecom_microservices.notify_service.dto;

import jakarta.validation.constraints.Email;
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
    @Email(message = "Email format looks incorrect! ")
    private String recipient;

    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message content must be at most 1000 characters")
    private String messageContent;

    @NotBlank(message = "Notification type is required")
    @Pattern(regexp = "EMAIL", message = "EMAIL is only accepted value for notification type")
    private String notificationType;

    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "HIGH|MEDIUM|LOW", message = "priority should be HIGH, MEDIUM, or LOW")
    private String priority;


}
