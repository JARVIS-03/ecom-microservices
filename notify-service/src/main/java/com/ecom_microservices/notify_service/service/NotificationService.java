// package com.ecom_microservices.notify_service.service;

// import com.ecom_microservices.notify_service.dto.NotificationRequestDTO;
// import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
// import com.ecom_microservices.notify_service.dto.OrderDTO;
// import com.ecom_microservices.notify_service.enums.NotificationStatus;
// import com.ecom_microservices.notify_service.enums.NotificationType;
// import com.ecom_microservices.notify_service.enums.OrderStatus;
// import com.ecom_microservices.notify_service.enums.PriorityLevel;
// import com.ecom_microservices.notify_service.exception.NotificationNotFoundException;
// import com.ecom_microservices.notify_service.model.Notification;
// import com.ecom_microservices.notify_service.repository.NotificationRepository;
// import com.ecom_microservices.notify_service.util.EmailSender;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// @Service
// public class NotificationService {
//     private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

//     private final NotificationRepository repository;

//     private final EmailSender emailSender;

//     public NotificationService(NotificationRepository repository, EmailSender emailSender) {
//         this.repository = repository;
//         this.emailSender = emailSender;
//     }

//     public NotificationResponseDTO createNotification(NotificationRequestDTO requestDTO) {
//         logger.info("Creating notification for recipient: {}", requestDTO.getRecipient());
//         Notification notification = Notification.builder()
//                 .recipient(requestDTO.getRecipient())
//                 .messageContent(requestDTO.getMessageContent())
//                 .type(Enum.valueOf(com.ecom_microservices.notify_service.enums.NotificationType.class, requestDTO.getNotificationType().toUpperCase()))
//                 .priority(Enum.valueOf(com.ecom_microservices.notify_service.enums.PriorityLevel.class, requestDTO.getPriority().toUpperCase()))
//                 .status(NotificationStatus.PENDING)
//                 .build();

//         Notification saved = repository.save(notification);
//         logger.debug("Notification saved with ID: {}", saved.getId());
//         return new NotificationResponseDTO(
//                 saved.getId(),
//                 saved.getRecipient(),
//                 saved.getMessageContent(),
//                 saved.getType().name(),
//                 saved.getPriority().name(),
//                 saved.getStatus().name(),
//                 saved.getCreatedTimestamp(),
//                 saved.getUpdatedTimestamp()
//         );
//     }

//     public NotificationResponseDTO scheduleNotification(NotificationRequestDTO requestDTO, LocalDateTime schedule) {
//         logger.info("Scheduling notification for recipient: {} at {}", requestDTO.getRecipient(), schedule);
//         Notification notification = Notification.builder()
//                 .recipient(requestDTO.getRecipient())
//                 .messageContent(requestDTO.getMessageContent())
//                 .type(Enum.valueOf(com.ecom_microservices.notify_service.enums.NotificationType.class, requestDTO.getNotificationType().toUpperCase()))
//                 .priority(Enum.valueOf(com.ecom_microservices.notify_service.enums.PriorityLevel.class, requestDTO.getPriority().toUpperCase()))
//                 .status(NotificationStatus.PENDING)
//                 .scheduledTime(schedule)
//                 .build();

//         Notification saved = repository.save(notification);
//         logger.debug("Notification scheduled with ID: {}", saved.getId());
//         return new NotificationResponseDTO(
//                 saved.getId(),
//                 saved.getRecipient(),
//                 saved.getMessageContent(),
//                 saved.getType().name(),
//                 saved.getPriority().name(),
//                 saved.getStatus().name(),
//                 saved.getCreatedTimestamp(),
//                 saved.getUpdatedTimestamp()
//         );
//     }

//     public NotificationResponseDTO createOrderStatusNotification(OrderDTO orderDTO) {
//         logger.info("Creating notification for recipient: {}", orderDTO.getUserEmail());

//         OrderStatus status = orderDTO.getStatus();
//         String message;
//         switch (status) {
//         	case NEW:
//         		message = "We Received your order. Your order Id is "+ orderDTO.getOrderId() +" Will start processing shortly!";
//         		break;
//         	case PROCESSING:
//         		message = "We are processing your order. please wait for further updates.";
//         		break;
//             case SHIPPED:
//                 message = "Your Order with Order Id: "+ orderDTO.getOrderId() +" is shipped successfully, It will be delivered Soon";
//                 break;
//             case DELIVERED:
//                 message = "Your Order with Order Id: "+ orderDTO.getOrderId() +" successfully delivered!";
//                 break;
//             case CANCELLED:
//             	message = "Your Order with Id: "+ orderDTO.getOrderId() +" is cancelled! ";
//             	break;
//             default:
//             	message ="Something Went Wrong!";
//             	break;
//         }

//         Notification notification = Notification.builder()
//                 .recipient(orderDTO.getUserEmail())
//                 .messageContent(message)
//                 .type(NotificationType.EMAIL)
//                 .priority((status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) ? PriorityLevel.HIGH : PriorityLevel.MEDIUM)
//                 .status(NotificationStatus.PENDING)
//                 .scheduledTime((status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) ? null : LocalDateTime.now().plusMinutes(5))
//                 .build();

//         Notification saved = repository.save(notification);
//         logger.debug("Notification saved with ID: {}", saved.getId());

//         if (saved.getPriority() == PriorityLevel.HIGH) {
//             emailSender.send(saved);
//         }

//         return new NotificationResponseDTO(
//                 saved.getId(),
//                 saved.getRecipient(),
//                 saved.getMessageContent(),
//                 saved.getType().name(),
//                 saved.getPriority().name(),
//                 saved.getStatus().name(),
//                 saved.getCreatedTimestamp(),
//                 saved.getUpdatedTimestamp()
//         );
//     }

//     public List<Notification> getNotificationsByStatus(NotificationStatus status) {
//         logger.info("Fetching notifications with status: {}", status);
//         List<Notification> notifications =  repository.findByStatus(status);
//         logger.debug("Fetched {} notifications with status {}", notifications.size(), status);
//         return notifications;
//     }
//     public Notification getNotificationById(Long id) {
//         Optional<Notification> opt = repository.findById(id);
//         if (opt.isEmpty()) {
//             logger.warn("Notification not found for ID: {}", id);

//             throw new NotificationNotFoundException("Notification not found with ID: " + id);
//         }
//         return opt.get();
//     }

//     public List<Notification> getNotificationsByRecipient(String recipient) {
//         logger.info("Fetching notifications for recipient: {}", recipient);
//         List<Notification> notifications = repository.findByRecipient(recipient);
//         logger.debug("Fetched {} notifications for recipient {}", notifications.size(), recipient);
//         return notifications;
//     }
//     public List<Notification> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
//         logger.info("Fetching notifications between {} and {}", startDate, endDate);
//         List<Notification> notifications =  repository.findByCreatedTimestampBetween(startDate, endDate);
//         logger.debug("Fetched {} notifications in date range", notifications.size());
//         return notifications;
//     }

// }

package com.ecom_microservices.notify_service.service;

import com.ecom_microservices.notify_service.dto.NotificationRequestDTO;
import com.ecom_microservices.notify_service.dto.NotificationResponseDTO;
import com.ecom_microservices.notify_service.dto.OrderDTO;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.enums.NotificationType;
import com.ecom_microservices.notify_service.enums.OrderStatus;
import com.ecom_microservices.notify_service.enums.PriorityLevel;
import com.ecom_microservices.notify_service.exception.InvalidNotificationRequestException;
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
    public NotificationResponseDTO createOrderStatusNotification(OrderDTO orderDTO) {
        logger.info("Creating notification for recipient: {}", orderDTO.getUserEmail());

        if (orderDTO.getStatus() == null) {
            throw new InvalidOrderStatusRequestException("Order status cannot be null");
        }

        String message = buildOrderMessage(orderDTO);
       
        Notification notification = buildOrderNotification(orderDTO,message);

        Notification saved = repository.save(notification);
        logger.debug("Notification saved with ID: {}", saved.getId());

        if (isHighPriority(orderDTO.getStatus())) {
            emailSender.send(saved);
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

    private boolean isHighPriority(OrderStatus st) {
        return st == OrderStatus.DELIVERED || st == OrderStatus.CANCELLED;
    }


  private Notification buildOrderNotification(OrderDTO orderDTO, String message) {
    try {
        OrderStatus status = orderDTO.getStatus();

            return Notification.builder()
                .recipient(orderDTO.getUserEmail())
                .messageContent(message)
                .type(NotificationType.EMAIL)
                .priority(isHighPriority(status)
                        ? PriorityLevel.HIGH
                        : PriorityLevel.MEDIUM)
                .status(NotificationStatus.PENDING)
                .scheduledTime(isHighPriority(status)
                        ? null
                        : LocalDateTime.now().plusMinutes(5))
                .build();

    }catch (Exception ex) {
        throw new InvalidOrderStatusRequestException("Error while building order notification.", ex);
    }
}

    

    private String buildOrderMessage(OrderDTO o) {
        return switch (o.getStatus()) {
            case NEW ->
                "We Received your order. Your order Id is " + o.getOrderId() + " Will start processing shortly!";
            case PROCESSING ->
                "We are processing your order id is " + o.getOrderId() + ". please wait for further updates.";
            case SHIPPED ->
                "Your Order with Order Id: " + o.getOrderId() + " is shipped successfully, It will be delivered Soon";
            case DELIVERED -> "Your Order with Order Id: " + o.getOrderId() + " successfully delivered!";
            case CANCELLED -> "Your Order with Id: " + o.getOrderId() + " is cancelled! ";
        };
    }

}