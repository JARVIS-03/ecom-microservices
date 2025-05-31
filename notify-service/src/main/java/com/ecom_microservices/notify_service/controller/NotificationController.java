package com.ecom_microservices.notify_service.controller;

import com.ecom_microservices.notify_service.dto.NotificationDto;
import com.ecom_microservices.notify_service.service.NotificationService;
import com.ecom_microservices.notify_service.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
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

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) Notification.Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Notification> notifications = notificationService.getNotifications(recipient, status, startDate, endDate);
        List<NotificationDto> dto = notifications.stream().map(this::toDto).collect(Collectors.toList());
        if (dto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    private NotificationDto toDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setRecipient(notification.getRecipient());
        dto.setMessageContent(notification.getMessageContent());
        dto.setNotificationType(notification.getNotificationType());
        dto.setPriority(notification.getPriority());
        dto.setStatus(notification.getStatus());
        dto.setScheduledTime(notification.getScheduledTime());
        dto.setCreatedTimestamp(notification.getCreatedTimestamp());
        dto.setUpdatedTimestamp(notification.getUpdatedTimestamp());
        return dto;
    }
}


