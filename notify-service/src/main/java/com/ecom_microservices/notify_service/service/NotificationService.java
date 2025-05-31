package com.ecom_microservices.notify_service.service;

import com.ecom_microservices.notify_service.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecom_microservices.notify_service.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getNotifications(String recipient, Notification.Status status, LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findByFilters(recipient, status, startDate, endDate);
    }
}


