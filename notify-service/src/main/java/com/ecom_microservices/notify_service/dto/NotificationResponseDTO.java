package com.ecom_microservices.notify_service.dto;


import java.time.LocalDateTime;

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
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }
    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public NotificationResponseDTO() {
    }
    public NotificationResponseDTO(Long id, String recipient, String messageContent, String notificationType,
            String priority, String status, LocalDateTime scheduledTime, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.notificationType = notificationType;
        this.priority = priority;
        this.status = status;
        this.scheduledTime = scheduledTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
}
