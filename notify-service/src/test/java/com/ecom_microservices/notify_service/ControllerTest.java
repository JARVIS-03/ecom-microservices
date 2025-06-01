package com.ecom_microservices.notify_service;

import com.ecom_microservices.notify_service.controller.NotificationController;
import com.ecom_microservices.notify_service.enums.NotificationStatus;
import com.ecom_microservices.notify_service.model.Notification;
import com.ecom_microservices.notify_service.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void testGetNotificationsByStatus_OK() throws Exception {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setRecipient("user1");
        notification.setStatus(NotificationStatus.SENT);
        Mockito.when(notificationService.getNotificationsByStatus(NotificationStatus.SENT))
                .thenReturn(List.of(notification));
        mockMvc.perform(get("/api/notifications/status/SENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipient").value("user1"));
    }

    @Test
    void testGetNotificationsByStatus_NoContent() throws Exception {
        Mockito.when(notificationService.getNotificationsByStatus(NotificationStatus.PENDING))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/notifications/status/PENDING"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetNotificationsByStatus_BadRequest() throws Exception {
        // Simulate invalid status (not in enum)
        mockMvc.perform(get("/api/notifications/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetNotificationsByStatus_NullStatus() throws Exception {
        // Simulate null status (should be handled as bad request)
        mockMvc.perform(get("/api/notifications/status/"))
                .andExpect(status().isNotFound()); // Path variable missing
    }

    @Test
    void testGetNotificationsByRecipient_OK() throws Exception {
        Notification notification = new Notification();
        notification.setId(2L);
        notification.setRecipient("user2");
        Mockito.when(notificationService.getNotificationsByRecipient("user2"))
                .thenReturn(List.of(notification));
        mockMvc.perform(get("/api/notifications/recipient/user2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipient").value("user2"));
    }

    @Test
    void testGetNotificationsByRecipient_NoContent() throws Exception {
        Mockito.when(notificationService.getNotificationsByRecipient("nobody"))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/notifications/recipient/nobody"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetNotificationsByRecipient_EmptyRecipient() throws Exception {
        mockMvc.perform(get("/api/notifications/recipient/"))
                .andExpect(status().isNotFound()); // Path variable missing
    }

    @Test
    void testGetNotificationsByDateRange_OK() throws Exception {
        Notification notification = new Notification();
        notification.setId(3L);
        notification.setRecipient("user3");
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        Mockito.when(notificationService.getNotificationsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(notification));
        mockMvc.perform(get("/api/notifications/daterange")
                .param("startDate", start.toString())
                .param("endDate", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipient").value("user3"));
    }

    @Test
    void testGetNotificationsByDateRange_NoContent() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Mockito.when(notificationService.getNotificationsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/notifications/daterange")
                .param("startDate", start.toString())
                .param("endDate", end.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetNotificationsByDateRange_InvalidDate() throws Exception {
        mockMvc.perform(get("/api/notifications/daterange")
                .param("startDate", "invalid-date")
                .param("endDate", "invalid-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetNotificationsByDateRange_MissingParams() throws Exception {
        mockMvc.perform(get("/api/notifications/daterange"))
                .andExpect(status().isBadRequest());
    }
}
