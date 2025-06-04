package com.ecom_microservices.notify_service.service;

import com.ecom_microservices.notify_service.dto.NotificationRequestDTO;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.dto.OrderDTO;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.enums.NotificationType;
import com.ecom_microservices.notify_service.enums.OrderStatus;
import com.ecom_microservices.notify_service.enums.PriorityLevel;
import com.ecom_microservices.notify_service.exception.NotificationNotFoundException;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.repository.NotificationRepository;
import com.ecom_microservices.notify_service.util.EmailSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public NotificationResponseDTO createNotification(NotificationRequestDTO requestDTO) {
        logger.info("Creating notification for recipient: {}", requestDTO.getRecipient());
        Notification notification = Notification.builder()
                .recipient(requestDTO.getRecipient())
                .messageContent(requestDTO.getMessageContent())
                .type(Enum.valueOf(com.ecom_microservices.notify_service.enums.NotificationType.class, requestDTO.getNotificationType().toUpperCase()))
                .priority(Enum.valueOf(com.ecom_microservices.notify_service.enums.PriorityLevel.class, requestDTO.getPriority().toUpperCase()))
                .status(NotificationStatus.PENDING)
                .build();

        Notification saved = repository.save(notification);
        logger.debug("Notification saved with ID: {}", saved.getId());
        return new NotificationResponseDTO(
                saved.getId(),
                saved.getRecipient(),
                saved.getMessageContent(),
                saved.getType().name(),
                saved.getPriority().name(),
                saved.getStatus().name(),
                saved.getCreatedTimestamp(),
                saved.getUpdatedTimestamp()
        );
    }
    
    public NotificationResponseDTO scheduleNotification(NotificationRequestDTO requestDTO, LocalDateTime schedule) {
        logger.info("Scheduling notification for recipient: {} at {}", requestDTO.getRecipient(), schedule);
        Notification notification = Notification.builder()
                .recipient(requestDTO.getRecipient())
                .messageContent(requestDTO.getMessageContent())
                .type(Enum.valueOf(com.ecom_microservices.notify_service.enums.NotificationType.class, requestDTO.getNotificationType().toUpperCase()))
                .priority(Enum.valueOf(com.ecom_microservices.notify_service.enums.PriorityLevel.class, requestDTO.getPriority().toUpperCase()))
                .status(NotificationStatus.PENDING)
                .scheduledTime(schedule)
                .build();

        Notification saved = repository.save(notification);
        logger.debug("Notification scheduled with ID: {}", saved.getId());
        return new NotificationResponseDTO(
                saved.getId(),
                saved.getRecipient(),
                saved.getMessageContent(),
                saved.getType().name(),
                saved.getPriority().name(),
                saved.getStatus().name(),
                saved.getCreatedTimestamp(),
                saved.getUpdatedTimestamp()
        );
    }
    
    public NotificationResponseDTO createOrderStatusNotification(OrderDTO orderDTO) {
        logger.info("Creating notification for recipient: {}", orderDTO.getUserEmail());

        OrderStatus status = orderDTO.getStatus();
        String message;
        switch (status) {
        	case NEW:
        		message = "We Received your order. Your order Id is "+ orderDTO.getOrderId() +" Will start processing shortly!";
        		break;
        	case PROCESSING:
        		message = "We are processing your order. please wait for further updates.";
        		break;
            case SHIPPED:
                message = "Your Order with Order Id: "+ orderDTO.getOrderId() +" is shipped successfully, It will be delivered Soon";
                break;
            case DELIVERED:
                message = "Your Order with Order Id: "+ orderDTO.getOrderId() +" successfully delivered!";
                break;
            case CANCELLED:
            	message = "Your Order with Id: "+ orderDTO.getOrderId() +" is cancelled! ";
            	break;
            default:
            	message ="Something Went Wrong!";
            	break;
        }

        Notification notification = Notification.builder()
                .recipient(orderDTO.getUserEmail())
                .messageContent(message)
                .type(NotificationType.EMAIL)
                .priority((status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) ? PriorityLevel.HIGH : PriorityLevel.MEDIUM)
                .status(NotificationStatus.PENDING)
                .scheduledTime((status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) ? null : LocalDateTime.now().plusMinutes(5))
                .build();

        Notification saved = repository.save(notification);
        logger.debug("Notification saved with ID: {}", saved.getId());

        if (saved.getPriority() == PriorityLevel.HIGH) {
            emailSender.send(saved);
        }

        return new NotificationResponseDTO(
                saved.getId(),
                saved.getRecipient(),
                saved.getMessageContent(),
                saved.getType().name(),
                saved.getPriority().name(),
                saved.getStatus().name(),
                saved.getCreatedTimestamp(),
                saved.getUpdatedTimestamp()
        );
    }

    public List<Notification> getNotificationsByStatus(NotificationStatus status) {
        logger.info("Fetching notifications with status: {}", status);
        List<Notification> notifications =  repository.findByStatus(status);
        logger.debug("Fetched {} notifications with status {}", notifications.size(), status);
        return notifications;
    }
    public Notification getNotificationById(Long id) {
        Optional<Notification> opt = repository.findById(id);
        if (opt.isEmpty()) {
            logger.warn("Notification not found for ID: {}", id);

            throw new NotificationNotFoundException("Notification not found with ID: " + id);
        }
        return opt.get();
    }



    public List<Notification> getNotificationsByRecipient(String recipient) {
        logger.info("Fetching notifications for recipient: {}", recipient);
        List<Notification> notifications = repository.findByRecipient(recipient);
        logger.debug("Fetched {} notifications for recipient {}", notifications.size(), recipient);
        return notifications;
    }
    public List<Notification> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Fetching notifications between {} and {}", startDate, endDate);
        List<Notification> notifications =  repository.findByCreatedTimestampBetween(startDate, endDate);
        logger.debug("Fetched {} notifications in date range", notifications.size());
        return notifications;
    }


}
