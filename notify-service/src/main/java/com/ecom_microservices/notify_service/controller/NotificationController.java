package com.ecom_microservices.notify_service.controller;

import com.ecom_microservices.notify_service.dto.NotificationRequestDTO;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationResponseDTO> sendNotification(@Valid @RequestBody NotificationRequestDTO requestDTO) {
        NotificationResponseDTO responseDTO = service.createNotification(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByStatus(@PathVariable String status) {
        NotificationStatus notificationStatus;

        try {
            notificationStatus = NotificationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build(); // invalid status string
        }

        List<Notification> notifications = service.getNotificationsByStatus(notificationStatus);
        List<NotificationResponseDTO> dtoList = notifications.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        if (dtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);
    }


    // New GET endpoint: By Recipient
    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByRecipient(@PathVariable String recipient) {
        List<Notification> notifications = service.getNotificationsByRecipient(recipient);
        List<NotificationResponseDTO> dtoList = notifications.stream().map(this::toResponseDto).collect(Collectors.toList());
        if (dtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);
    }
    @GetMapping("/date-range")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Notification> notifications = service.getNotificationsByDateRange(startDate, endDate);
        List<NotificationResponseDTO> dto = notifications.stream().map(this::toResponseDto).collect(Collectors.toList());
        if (dto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    private NotificationResponseDTO toResponseDto(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getRecipient(),
                notification.getMessageContent(),
                notification.getType().name(),
                notification.getPriority().name(),
                notification.getStatus().name(),
                notification.getCreatedTimestamp(),
                notification.getUpdatedTimestamp()
        );
    }
}


