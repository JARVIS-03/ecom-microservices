package com.ecom_microservices.notify_service;

import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.enums.NotificationType;
import com.ecom_microservices.notify_service.enums.PriorityLevel;
import com.ecom_microservices.notify_service.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepositoryTest {
    @Mock
    private NotificationRepository notificationRepository;

    @Test
    void testFindByStatus() {
        Notification n1 = new Notification();
        n1.setRecipient("user1@example.com");
        n1.setStatus(NotificationStatus.SENT);
        Notification n2 = new Notification();
        n2.setRecipient("user2@example.com");
        n2.setStatus(NotificationStatus.PENDING);
        when(notificationRepository.findByStatus(NotificationStatus.SENT)).thenReturn(List.of(n1));
        List<Notification> sent = notificationRepository.findByStatus(NotificationStatus.SENT);
        assertTrue(sent.stream().anyMatch(n -> n.getRecipient().equals("user1@example.com")));
        assertFalse(sent.stream().anyMatch(n -> n.getRecipient().equals("user2@example.com")));
        verify(notificationRepository, times(1)).findByStatus(NotificationStatus.SENT);
    }

    @Test
    void testFindByRecipient() {
        Notification n1 = new Notification();
        n1.setRecipient("findme@example.com");
        when(notificationRepository.findByRecipient("findme@example.com")).thenReturn(List.of(n1));
        List<Notification> found = notificationRepository.findByRecipient("findme@example.com");
        assertEquals(1, found.size());
        assertEquals("findme@example.com", found.get(0).getRecipient());
        verify(notificationRepository, times(1)).findByRecipient("findme@example.com");
    }

    @Test
    void testFindByCreatedTimestampBetween() {
        Notification n1 = new Notification();
        n1.setRecipient("user3@example.com");
        Notification n2 = new Notification();
        n2.setRecipient("user4@example.com");
        LocalDateTime now = LocalDateTime.now();
        when(notificationRepository.findByCreatedTimestampBetween(now.minusDays(3), now.minusDays(1))).thenReturn(List.of(n1));
        List<Notification> found = notificationRepository.findByCreatedTimestampBetween(now.minusDays(3), now.minusDays(1));
        assertTrue(found.stream().anyMatch(n -> n.getRecipient().equals("user3@example.com")));
        assertFalse(found.stream().anyMatch(n -> n.getRecipient().equals("user4@example.com")));
        verify(notificationRepository, times(1)).findByCreatedTimestampBetween(now.minusDays(3), now.minusDays(1));
    }

    @Test
    void testFindByStatus_NoResults() {
        when(notificationRepository.findByStatus(NotificationStatus.FAILED)).thenReturn(List.of());
        List<Notification> found = notificationRepository.findByStatus(NotificationStatus.FAILED);
        assertTrue(found.isEmpty());
        verify(notificationRepository, times(1)).findByStatus(NotificationStatus.FAILED);
    }

    @Test
    void testFindByRecipient_NoResults() {
        when(notificationRepository.findByRecipient("notfound@example.com")).thenReturn(List.of());
        List<Notification> found = notificationRepository.findByRecipient("notfound@example.com");
        assertTrue(found.isEmpty());
        verify(notificationRepository, times(1)).findByRecipient("notfound@example.com");
    }

    @Test
    void testFindByCreatedTimestampBetween_NoResults() {
        LocalDateTime now = LocalDateTime.now();
        when(notificationRepository.findByCreatedTimestampBetween(now.minusYears(1), now.minusMonths(11))).thenReturn(List.of());
        List<Notification> found = notificationRepository.findByCreatedTimestampBetween(now.minusYears(1), now.minusMonths(11));
        assertTrue(found.isEmpty());
        verify(notificationRepository, times(1)).findByCreatedTimestampBetween(now.minusYears(1), now.minusMonths(11));
    }
}
