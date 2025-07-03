package com.ecom_microservices.notify_service.service;

import com.ecom_microservices.notify_service.dto.NotificationRequestDTO;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.dto.NotificationOrderDTO;
import com.ecom_microservices.notify_service.dto.NotificationPaymentDTO;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.enums.NotificationType;
import com.ecom_microservices.notify_service.enums.OrderStatus;
import com.ecom_microservices.notify_service.enums.PaymentStatus;
import com.ecom_microservices.notify_service.enums.PriorityLevel;
import com.ecom_microservices.notify_service.exception.InvalidOrderStatusRequestException;
import com.ecom_microservices.notify_service.exception.NotificationNotFoundException;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.repository.NotificationRepository;
import com.ecom_microservices.notify_service.util.EmailSender;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;

    private final EmailSender emailSender;

    public NotificationService(NotificationRepository repository, EmailSender emailSender) {
        this.repository = repository;
        this.emailSender = emailSender;
    }

    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO requestDTO) {
        logger.info("Creating notification for recipient: {}", requestDTO.getRecipient());
        // Notification notification = Notification.builder()
        // .recipient(requestDTO.getRecipient())
        // .messageContent(requestDTO.getMessageContent())
        // .type(Enum.valueOf(com.ecom_microservices.notify_service.enums.NotificationType.class,
        // requestDTO.getNotificationType().toUpperCase()))
        // .priority(Enum.valueOf(com.ecom_microservices.notify_service.enums.PriorityLevel.class,
        // requestDTO.getPriority().toUpperCase()))
        // .status(NotificationStatus.PENDING)
        // .build();
        Notification notification = buildEntity(requestDTO, null);

        Notification saved = repository.save(notification);
        logger.debug("Notification saved with ID: {}", saved.getId());
        // return new NotificationResponseDTO(
        // saved.getId(),
        // saved.getRecipient(),
        // saved.getMessageContent(),
        // saved.getType().name(),
        // saved.getPriority().name(),
        // saved.getStatus().name(),
        // saved.getCreatedTimestamp(),
        // saved.getUpdatedTimestamp());
        return toDto(saved);
    }

    @Transactional
    public NotificationResponseDTO scheduleNotification(NotificationRequestDTO requestDTO, LocalDateTime schedule) {
        logger.info("Scheduling notification for recipient: {} at {}", requestDTO.getRecipient(), schedule);

        Notification notification = buildEntity(requestDTO, schedule);

        Notification saved = repository.save(notification);
        logger.debug("Notification scheduled with ID: {}", saved.getId());

        return toDto(saved);
    }

    @Transactional
    public NotificationResponseDTO createOrderStatusNotification(NotificationOrderDTO orderDTO) {
        logger.info("Creating notification for recipient: {}", orderDTO.getUserEmail());

        if (orderDTO.getStatus() == null) {
            throw new InvalidOrderStatusRequestException("Order status cannot be null");
        }

        String message = buildOrderMessage(orderDTO);

        Notification notification = buildOrderNotification(orderDTO, message);

        Notification saved = repository.save(notification);
        logger.debug("Notification saved with ID: {}", saved.getId());

        if (isHighOrderPriority(orderDTO.getStatus())) {
            emailSender.send(saved);
            logger.info("mail send successfuly with order Id : "+orderDTO.getOrderId());
        }

        return toDto(saved);
    }

    @Transactional
    public NotificationResponseDTO createPaymentStatusNotification(NotificationPaymentDTO notificationPaymentDTO) {
        logger.info("Creating notification for recipient: {}", notificationPaymentDTO.getUserEmail());

        if (notificationPaymentDTO.getStatus() == null) {
            throw new InvalidOrderStatusRequestException("Order status cannot be null");
        }

        String message = buildPaymentMessage(notificationPaymentDTO);

        Notification notification = buildPaymentNotification(notificationPaymentDTO, message);

        Notification saved = repository.save(notification);
        logger.debug("Notification saved with ID: {}", saved.getId());

        if (isHighPaymentPriority(notificationPaymentDTO.getStatus())) {
            emailSender.send(saved);
            logger.info("mail send successfuly with payment Id : "+notificationPaymentDTO.getPaymentId());
        }
        
       
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByStatus(NotificationStatus status) {
        logger.info("Fetching notifications with status: {}", status);
        List<Notification> notifications = repository.findByStatus(status);
        logger.debug("Fetched {} notifications with status {}", notifications.size(), status);
        return notifications;
    }

    @Transactional(readOnly = true)
    public Notification getNotificationById(Long id) {
        Optional<Notification> opt = repository.findById(id);
        if (opt.isEmpty()) {
            logger.warn("Notification not found for ID: {}", id);

            throw new NotificationNotFoundException("Notification not found with ID: " + id);
        }
        return opt.get();
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByRecipient(String recipient) {
        logger.info("Fetching notifications for recipient: {}", recipient);
        List<Notification> notifications = repository.findByRecipient(recipient);
        logger.debug("Fetched {} notifications for recipient {}", notifications.size(), recipient);
        return notifications;
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Fetching notifications between {} and {}", startDate, endDate);
        List<Notification> notifications = repository.findByCreatedTimestampBetween(startDate, endDate);
        logger.debug("Fetched {} notifications in date range", notifications.size());
        return notifications;
    }

    private Notification buildEntity(NotificationRequestDTO dto,
            LocalDateTime schedule) {
        return Notification.builder()
                .recipient(dto.getRecipient())
                .messageContent(dto.getMessageContent())
                .type(NotificationType.valueOf(dto.getNotificationType().toUpperCase()))
                .priority(PriorityLevel.valueOf(dto.getPriority().toUpperCase()))
                .status(NotificationStatus.PENDING)
                .scheduledTime(schedule)
                .build();
    }

    private NotificationResponseDTO toDto(Notification n) {
        return new NotificationResponseDTO(
                n.getId(), n.getRecipient(), n.getMessageContent(),
                n.getType().name(), n.getPriority().name(), n.getStatus().name(),
                n.getCreatedTimestamp(), n.getUpdatedTimestamp());
    }

    private boolean isHighOrderPriority(OrderStatus st) {
        return st == OrderStatus.DELIVERED || st == OrderStatus.CANCELLED;
    }

    private boolean isHighPaymentPriority(PaymentStatus st) {
        return st == PaymentStatus.SUCCESS || st == PaymentStatus.FAILED || st == PaymentStatus.REFUNDED;
    }

    private Notification buildOrderNotification(NotificationOrderDTO orderDTO, String message) {
        try {
            OrderStatus status = orderDTO.getStatus();

            return Notification.builder()
                    .recipient(orderDTO.getUserEmail())
                    .messageContent(message)
                    .type(NotificationType.EMAIL)
                    .priority(isHighOrderPriority(status)
                            ? PriorityLevel.HIGH
                            : PriorityLevel.MEDIUM)
                    .status(NotificationStatus.PENDING)
                    .scheduledTime(isHighOrderPriority(status)
                            ? null
                            : LocalDateTime.now().plusMinutes(5))
                    .orderId(orderDTO.getOrderId())
                    .build();

        } catch (Exception ex) {
            throw new InvalidOrderStatusRequestException("Error while building order notification.", ex);
        }
    }

    private Notification buildPaymentNotification(NotificationPaymentDTO p, String message) {
        try {
            PaymentStatus status = p.getStatus();

            return Notification.builder()
                    .recipient(p.getUserEmail())
                    .messageContent(message)
                    .type(NotificationType.EMAIL)
                    .priority(isHighPaymentPriority(status)
                            ? PriorityLevel.HIGH
                            : PriorityLevel.MEDIUM)
                    .status(NotificationStatus.PENDING)
                    .scheduledTime(isHighPaymentPriority(status)
                            ? null
                            : LocalDateTime.now().plusMinutes(5))
                    .paymentId(p.getPaymentId())
                    .build();

        } catch (Exception ex) {
            throw new InvalidOrderStatusRequestException("Error while building payment notification.", ex);
        }
    }

    private String buildOrderMessage(NotificationOrderDTO o) {
        return switch (o.getStatus()) {
            case NEW ->
                "We have received your order. Your Order ID is " + o.getOrderId() + ". Processing will start shortly.";
            case PROCESSING ->
                "Your order (Order ID: " + o.getOrderId()
                        + ") is currently being processed. Please wait for further updates.";
            case SHIPPED ->
                "Your order (Order ID: " + o.getOrderId() + ") has been shipped. It will be delivered soon.";
            case DELIVERED ->
                "Your order (Order ID: " + o.getOrderId() + ") has been delivered successfully!";
            case CANCELLED ->
                "Your order (Order ID: " + o.getOrderId() + ") has been cancelled.";
        };
    }

    private String buildPaymentMessage(NotificationPaymentDTO p) {
        return switch (p.getStatus()) {
            case INITIATED ->
                "Your payment for Payment ID " + p.getPaymentId() + " has been initiated.";
            case PROCESSING ->
                "Your payment for Payment ID " + p.getPaymentId() + " is currently being processed.";
            case SUCCESS ->
                "Payment successful for Payment ID " + p.getPaymentId() + ". Thank you for your purchase!";
            case FAILED ->
                "Payment failed for Payment ID " + p.getPaymentId() + ". Please try again.";
            case REFUNDED ->
                "Your payment for Payment ID " + p.getPaymentId() + " has been refunded.";
        };
    }

}