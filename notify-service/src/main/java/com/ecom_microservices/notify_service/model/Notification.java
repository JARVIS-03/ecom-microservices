package com.ecom_microservices.notify_service.model;



import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.enums.NotificationType;
import com.ecom_microservices.notify_service.enums.PriorityLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import org.hibernate.validator.constraints.NotBlank;


import java.time.LocalDateTime;
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = true)
    private Long orderId;

    @Column(name = "payment_id", nullable = true)
    private Long paymentId;

    @NotBlank(message = "Recipient must not be empty")
    @Email(message = "Invalid email format") // Valid only for EMAIL type
    @Column(nullable = false)
    private String recipient;

    @NotBlank(message = "Message content must not be empty")
    @Size(max = 1000, message = "Message content must be at most 1000 characters")
    @Column(name = "message_content", nullable = false, length = 1000)
    private String messageContent;

    @NotNull(message = "Priority must be specified")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityLevel priority;

    @NotNull(message = "Status must be specified")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @NotNull(message = "Notification type must be specified")
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType type = NotificationType.EMAIL;

    //@Future(message = "Scheduled time must be in the future")
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "updated_timestamp")
    private LocalDateTime updatedTimestamp;

    @PrePersist
    protected void onCreate() {
        createdTimestamp = LocalDateTime.now();
        updatedTimestamp = createdTimestamp;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTimestamp = LocalDateTime.now();
    }
}
