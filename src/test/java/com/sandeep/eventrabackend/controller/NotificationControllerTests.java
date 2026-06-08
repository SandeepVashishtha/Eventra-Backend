package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.model.Notification;
import com.sandeep.eventrabackend.model.Role;
import com.sandeep.eventrabackend.model.User;
import com.sandeep.eventrabackend.repository.NotificationRepository;
import com.sandeep.eventrabackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NotificationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();

        testUser1 = User.builder()
                .firstName("User1")
                .lastName("Test")
                .email("user1@example.com")
                .username("user1")
                .password("password")
                .role(Role.CLIENT)
                .build();
        userRepository.save(testUser1);

        testUser2 = User.builder()
                .firstName("User2")
                .lastName("Test")
                .email("user2@example.com")
                .username("user2")
                .password("password")
                .role(Role.CLIENT)
                .build();
        userRepository.save(testUser2);
    }

    @Test
    @DisplayName("GET /api/notifications returns empty list for user with no notifications")
    void testGetNotificationsEmpty() throws Exception {
        mockMvc.perform(get("/api/notifications")
                        .with(user("user1@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/notifications returns 401 for unauthenticated request")
    void testGetNotificationsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/notifications returns notifications for authenticated user")
    void testGetNotificationsSuccess() throws Exception {
        Notification n1 = Notification.builder()
                .user(testUser1)
                .title("Title 1")
                .message("Message 1")
                .isRead(false)
                .build();
        notificationRepository.save(n1);

        mockMvc.perform(get("/api/notifications")
                        .with(user("user1@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[0].message").value("Message 1"))
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    @DisplayName("GET /api/notifications filters notifications by user")
    void testGetNotificationsIsolation() throws Exception {
        Notification n1 = Notification.builder()
                .user(testUser1)
                .title("User 1 Notification")
                .message("Secret")
                .isRead(false)
                .build();
        notificationRepository.save(n1);

        Notification n2 = Notification.builder()
                .user(testUser2)
                .title("User 2 Notification")
                .message("Secret")
                .isRead(false)
                .build();
        notificationRepository.save(n2);

        mockMvc.perform(get("/api/notifications")
                        .with(user("user1@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("User 1 Notification"));
    }

    @Test
    @DisplayName("GET /api/notifications returns notifications sorted by newest first")
    void testGetNotificationsSorting() throws Exception {
        Notification oldNotification = Notification.builder()
                .user(testUser1)
                .title("Old")
                .message("Old Message")
                .isRead(false)
                .build();
        notificationRepository.save(oldNotification);

        // Manually set creation time if possible, or just rely on sequence of saves
        // Since we use @CreationTimestamp, the second save should have a later timestamp.
        
        Notification newNotification = Notification.builder()
                .user(testUser1)
                .title("New")
                .message("New Message")
                .isRead(false)
                .build();
        notificationRepository.save(newNotification);

        mockMvc.perform(get("/api/notifications")
                        .with(user("user1@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("New"))
                .andExpect(jsonPath("$[1].title").value("Old"));
    }
}
