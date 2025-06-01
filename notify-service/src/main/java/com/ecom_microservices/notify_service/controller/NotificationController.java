package com.ecom_microservices.notify_service.controller;

import com.ecom_microservices.notify_service.dto.NotificationDto;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.service.NotificationService;
import com.ecom_microservices.notify_service.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }



    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        if(status == null || !Arrays.asList(NotificationStatus.values()).contains(status)) {
            return ResponseEntity.badRequest().body(null);
        }
        List<Notification> notifications = notificationService.getNotificationsByStatus(status);
        List<NotificationDto> dto = notifications.stream().map(this::toDto).collect(Collectors.toList());
        if (dto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByRecipient(@PathVariable String recipient) {
        List<Notification> notifications = notificationService.getNotificationsByRecipient(recipient);
        List<NotificationDto> dto = notifications.stream().map(this::toDto).collect(Collectors.toList());
        if (dto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/daterange")
    public ResponseEntity<List<NotificationDto>> getNotificationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Notification> notifications = notificationService.getNotificationsByDateRange(startDate, endDate);
        List<NotificationDto> dto = notifications.stream().map(this::toDto).collect(Collectors.toList());
        if (dto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    private NotificationDto toDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getId());
        dto.setRecipient(notification.getRecipient());
        dto.setMessageContent(notification.getMessageContent());
        dto.setNotificationType(notification.getType());
        dto.setPriority(notification.getPriority());
        dto.setStatus(notification.getStatus());
        dto.setScheduledTime(notification.getScheduledTime());
        dto.setCreatedTimestamp(notification.getCreatedTimestamp());
        dto.setUpdatedTimestamp(notification.getUpdatedTimestamp());
        return dto;
    }
}

