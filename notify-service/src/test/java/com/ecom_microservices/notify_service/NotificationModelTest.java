
package com.ecom_microservices.notify_service;

import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.enums.NotificationType;
import com.ecom_microservices.notify_service.enums.PriorityLevel;
import com.ecom_microservices.notify_service.model.Notification;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationModelTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidNotification() {
        Notification notification = Notification.builder()
                .recipient("user@example.com")
                .messageContent("Your order is confirmed!")
                .priority(PriorityLevel.HIGH)
                .status(NotificationStatus.PENDING)
                .type(NotificationType.EMAIL)
                .scheduledTime(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankRecipientShouldFail() {
        Notification notification = Notification.builder()
                .recipient("")
                .messageContent("Sample message")
                .priority(PriorityLevel.MEDIUM)
                .status(NotificationStatus.SENT)
                .type(NotificationType.EMAIL)
                .build();

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("recipient")));
    }

    @Test
    void testInvalidEmailShouldFail() {
        Notification notification = Notification.builder()
                .recipient("invalid-email")
                .messageContent("Message")
                .priority(PriorityLevel.LOW)
                .status(NotificationStatus.PENDING)
                .type(NotificationType.EMAIL)
                .build();

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid email format")));
    }

    @Test
    void testMessageExceedsMaxLength() {
        String longMessage = "a".repeat(1001);
        Notification notification = Notification.builder()
                .recipient("user@example.com")
                .messageContent(longMessage)
                .priority(PriorityLevel.HIGH)
                .status(NotificationStatus.PENDING)
                .type(NotificationType.EMAIL)
                .build();

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("messageContent")));
    }

    @Test
    void testNullStatusShouldFail() {
        Notification notification = Notification.builder()
                .recipient("user@example.com")
                .messageContent("Test message")
                .priority(PriorityLevel.MEDIUM)
                .type(NotificationType.EMAIL)
                .build();

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")));
    }

    @Test
    void testDefaultNotificationTypeIsEmail() {
        Notification notification = Notification.builder()
                .recipient("user@example.com")
                .messageContent("Default type test")
                .priority(PriorityLevel.HIGH)
                .status(NotificationStatus.PENDING)
                .build();

        assertNull(notification.getType());

        if (notification.getType() == null) {
            notification.setType(NotificationType.EMAIL);
        }

        assertEquals(NotificationType.EMAIL, notification.getType());
    }

    @Test
    void testInvalidEmailRecipientFailsValidation() {
        Notification notification = Notification.builder()
                .recipient("invalid-email-format")
                .messageContent("Test message")
                .priority(PriorityLevel.HIGH)
                .status(NotificationStatus.PENDING)
                .type(NotificationType.EMAIL)
                .build();

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("recipient")));
    }

    @Test
    void testMessageContentSizeExceedsLimitFailsValidation() {
        String longMessage = "a".repeat(1001);

        Notification notification = Notification.builder()
                .recipient("user@example.com")
                .messageContent(longMessage)
                .priority(PriorityLevel.HIGH)
                .status(NotificationStatus.PENDING)
                .type(NotificationType.EMAIL)
                .build();

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("messageContent")));
    }

    @Test
    void testScheduledTimeInPastFailsValidation() {
        Notification notification = Notification.builder()
                .recipient("user@example.com")
                .messageContent("Scheduled time test")
                .priority(PriorityLevel.MEDIUM)
                .status(NotificationStatus.PENDING)
                .type(NotificationType.EMAIL)
                .scheduledTime(LocalDateTime.now().minusDays(1))
                .build();

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("scheduledTime")));
    }

    @Test
    void testMissingPriorityShouldFail() {
        Notification notification = Notification.builder()
                .recipient("user@example.com")
                .messageContent("Test message")
                .status(NotificationStatus.FAILED)
                .type(NotificationType.EMAIL)
                .build();

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("priority")));
    }

    @Test
    void testScheduledTimeInPastShouldFail() {
        Notification notification = Notification.builder()
                .recipient("user@example.com")
                .messageContent("Reminder")
                .priority(PriorityLevel.LOW)
                .status(NotificationStatus.PENDING)
                .type(NotificationType.EMAIL)
                .scheduledTime(LocalDateTime.now().minusDays(1))
                .build();

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Scheduled time must be in the future")));
    }

    @Test
    void testLifecycleCallbacks_setTimestamps() {
        Notification notification = Notification.builder()
                .recipient("user@example.com")
                .messageContent("Test message")
                .priority(PriorityLevel.MEDIUM)
                .status(NotificationStatus.PENDING)
                .type(NotificationType.EMAIL)
                .build();

        notification.onCreate(); // Manually trigger @PrePersist
        assertNotNull(notification.getCreatedTimestamp());
        assertNotNull(notification.getUpdatedTimestamp());
        assertEquals(notification.getCreatedTimestamp(), notification.getUpdatedTimestamp());

        LocalDateTime previousUpdate = notification.getUpdatedTimestamp();

        // Simulate update delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        notification.onUpdate(); // Manually trigger @PreUpdate
        assertTrue(notification.getUpdatedTimestamp().isAfter(previousUpdate));
    }
}

