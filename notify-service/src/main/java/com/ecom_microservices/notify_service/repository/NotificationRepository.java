package com.ecom_microservices.notify_service.repository;

import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification save(Notification notification);
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByRecipient(String recipient);
    List<Notification> findByCreatedTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query(value = "SELECT n FROM Notification n WHERE n.status = :status " +
            "ORDER BY CASE n.priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MEDIUM' THEN 2 " +
            "WHEN 'LOW' THEN 3 END," +
            "n.createdTimestamp ASC")
    List<Notification> findNotificationByStatusOrderByPriority(@Param("status") NotificationStatus status, Pageable pageable);
}
