package com.ecom_microservices.notify_service.repository;

import com.ecom_microservices.notify_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE (:recipient IS NULL OR n.recipient = :recipient) " +
            "AND (:status IS NULL OR n.status = :status) " +
            "AND (:startDate IS NULL OR n.createdTimestamp >= :startDate) " +
            "AND (:endDate IS NULL OR n.createdTimestamp <= :endDate)")
    List<Notification> findByFilters(@Param("recipient") String recipient,
                                     @Param("status") Notification.Status status,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}

