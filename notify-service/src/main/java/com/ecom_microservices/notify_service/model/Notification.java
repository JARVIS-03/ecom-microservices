
package com.ecom_microservices.notify_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    private String recipient;
    private String messageContent;
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;



    public enum NotificationType {
        EMAIL, SMS, PUSH
    }
    public enum Priority {
        HIGH, MEDIUM, LOW
    }
    public enum Status {
        PENDING, SENT, RETRIED
    }
}

