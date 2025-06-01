package com.ecom_microservices.notify_service.repository;

import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByRecipient(String recipient);
    List<Notification> findByCreatedTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
}
