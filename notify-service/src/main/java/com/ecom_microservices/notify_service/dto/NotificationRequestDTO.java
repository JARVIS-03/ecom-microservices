package com.ecom_microservices.notify_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class NotificationRequestDTO {

    @NotBlank(message = "Recipient is required")
    private String recipient;

    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message content must be at most 1000 characters")
    private String messageContent;

    @NotBlank(message = "Notification type is required")
    @Pattern(regexp = "Email", message = "Notification type must be Email, SMS, or Push")
    private String notificationType;

    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "HIGH|MEDIUM|LOW", message = "Priority must be HIGH, MEDIUM, or LOW")
    private String priority;

    private String scheduledTime;

    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessageContent() {
        return messageContent;
    }
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getNotificationType() {
        return notificationType;
    }
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }
    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
