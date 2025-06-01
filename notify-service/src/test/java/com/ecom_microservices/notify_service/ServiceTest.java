package com.ecom_microservices.notify_service;

import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.repository.NotificationRepository;
import com.ecom_microservices.notify_service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.apache.logging.log4j.ThreadContext.isEmpty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceTest {
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNotificationsByStatus() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setStatus(NotificationStatus.SENT);
        when(notificationRepository.findByStatus(NotificationStatus.SENT)).thenReturn(List.of(notification));
        List<Notification> result = notificationService.getNotificationsByStatus(NotificationStatus.SENT);
        assertEquals(1, result.size());
        assertEquals(NotificationStatus.SENT, result.get(0).getStatus());
        verify(notificationRepository, times(1)).findByStatus(NotificationStatus.SENT);
    }

    @Test
    void testGetNotificationsByRecipient() {
        Notification notification = new Notification();
        notification.setId(2L);
        notification.setRecipient("user@example.com");
        when(notificationRepository.findByRecipient("user@example.com")).thenReturn(List.of(notification));
        List<Notification> result = notificationService.getNotificationsByRecipient("user@example.com");
        assertEquals(1, result.size());
        assertEquals("user@example.com", result.get(0).getRecipient());
        verify(notificationRepository, times(1)).findByRecipient("user@example.com");
    }

    @Test
    void testGetNotificationsByDateRange() {
        Notification notification = new Notification();
        notification.setId(3L);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(notificationRepository.findByCreatedTimestampBetween(start, end)).thenReturn(List.of(notification));
        List<Notification> result = notificationService.getNotificationsByDateRange(start, end);
        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findByCreatedTimestampBetween(start, end);
    }

    @Test
    void testGetNotificationsByStatus_Empty() {
        when(notificationRepository.findByStatus(NotificationStatus.PENDING)).thenReturn(Collections.emptyList());
        List<Notification> result = notificationService.getNotificationsByStatus(NotificationStatus.PENDING);
        assertTrue(result.isEmpty());
        verify(notificationRepository, times(1)).findByStatus(NotificationStatus.PENDING);
    }

}
