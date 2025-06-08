package com.ecom_microservices.notify_service.controller;

import com.ecom_microservices.notify_service.dto.NotificationRequestDTO;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.dto.OrderDTO;
import com.ecom_microservices.notify_service.dto.PaymentDTO;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.service.NotificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationResponseDTO> sendNotification(@Valid @RequestBody NotificationRequestDTO requestDTO) {
    	logger.info("POST /api/notifications/send - Sending notification to '{}'", requestDTO.getRecipient());
        NotificationResponseDTO responseDTO = service.createNotification(requestDTO);
        logger.info("Notification created successfully with ID {}", responseDTO.getId());
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    
    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleNotification(@Valid @RequestBody NotificationRequestDTO requestDTO,
            @RequestParam("datetime") @DateTimeFormat(pattern = "dd-MM-yyyy-HH-mm-ss") LocalDateTime datetime) {
    	if (datetime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Scheduled datetime must be in the future.");
        }
    	logger.info("POST /api/notifications/send - Sending notification to '{}'", requestDTO.getRecipient());
        NotificationResponseDTO responseDTO = service.scheduleNotification(requestDTO, datetime);
        logger.info("Notification scheduled successfully with ID {}", responseDTO.getId());
        return ResponseEntity.ok(responseDTO);
    }
    
    @PostMapping("/order/send")
    public ResponseEntity<NotificationResponseDTO> sendOrderStatusNotification(@Valid @RequestBody OrderDTO orderDTO) {
    	logger.info("POST /api/notifications/order/send - Sending notification to '{}'", orderDTO.getUserEmail());
    	NotificationResponseDTO responseDTO = service.createOrderStatusNotification(orderDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    @PostMapping("/payment/send")
    public ResponseEntity<NotificationResponseDTO> sendPaymentStatusNotification(@Valid @RequestBody PaymentDTO paymentDTO) {
        logger.info("POST /api/notifications/payment/send - Sending notification to '{}'", paymentDTO.getUserEmail());
        NotificationResponseDTO response = service.createPaymentStatusNotification(paymentDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getNotificationStatusById(@PathVariable Long id) {
        logger.info("GET /api/notifications/{}/status - Fetching notification status", id);
        Notification notification = service.getNotificationById(id); // will throw if not found
        logger.info("Notification status for ID {} is '{}'", id, notification.getStatus());
        return ResponseEntity.ok(notification.getStatus().name());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByStatus(@PathVariable String status) {
        logger.info("GET /api/notifications/status/{} - Fetching notifications by status", status);
        NotificationStatus notificationStatus;
        notificationStatus = NotificationStatus.valueOf(status.toUpperCase());
        List<Notification> notifications = service.getNotificationsByStatus(notificationStatus);
        List<NotificationResponseDTO> dtoList = notifications.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
        if (dtoList.isEmpty()) {
            logger.info("No notifications found with status '{}'", status);
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning {} notifications with status '{}'", dtoList.size(), status);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByRecipient(@PathVariable String recipient) {
        logger.info("Received request to fetch notifications for recipient: {}", recipient);
        List<Notification> notifications = service.getNotificationsByRecipient(recipient);
        List<NotificationResponseDTO> dtoList = notifications.stream().map(this::toResponseDto).collect(Collectors.toList());
        if (dtoList.isEmpty()) {
            logger.info("No notifications found for recipient: {}", recipient);
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning {} notifications for recipient: {}", dtoList.size(), recipient);
        return ResponseEntity.ok(dtoList);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        logger.info("GET /api/notifications/date-range?startDate={}&endDate={} - Fetching notifications by date range", startDate, endDate);
        List<Notification> notifications = service.getNotificationsByDateRange(startDate, endDate);
        List<NotificationResponseDTO> dto = notifications.stream().map(this::toResponseDto).collect(Collectors.toList());
        if (dto.isEmpty()) {
            logger.info("No notifications found between {} and {}", startDate, endDate);
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning {} notifications between {} and {}", dto.size(), startDate, endDate);
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


