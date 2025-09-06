package com.eventra.controller;

import com.eventra.entity.User;
import com.eventra.exception.UnauthorizedAccessException;
import com.eventra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserInfo>> getAllUsers() {
        log.info("Received request to get all users");
        
        // Check if user is authenticated and has admin role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getName())) {
            log.warn("Unauthorized access attempt to admin endpoint");
            throw new UnauthorizedAccessException("Authentication required to access admin endpoints");
        }
        
        try {
            List<User> users = userRepository.findAll();
            List<UserInfo> userInfos = users.stream()
                    .map(user -> new UserInfo(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getCreatedAt(),
                        user.getEnabled(),
                        // Temporarily return empty list since roles are not fully implemented
                        List.of() // user.getRoles().stream()
                                  // .map(role -> role.getName().name())
                                  // .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} users for admin", userInfos.size());
            return ResponseEntity.ok(userInfos);
        } catch (Exception e) {
            log.error("Error retrieving users: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve users. Please try again.");
        }
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventInfo>> getAllEvents() {
        // For now, return sample data since we don't have Event entity yet
        List<EventInfo> events = List.of(
            new EventInfo(1L, "Sample Event 1", "2025-08-15", 25, "Active"),
            new EventInfo(2L, "Sample Event 2", "2025-08-20", 40, "Active"),
            new EventInfo(3L, "Sample Event 3", "2025-08-25", 15, "Draft")
        );
        return ResponseEntity.ok(events);
    }

    // Inner class for user info without password
    public static class UserInfo {
        public Long id;
        public String email;
        public String firstName;
        public String lastName;
        public java.time.LocalDateTime createdAt;
        public Boolean enabled;
        public java.util.List<String> roles;

        public UserInfo(Long id, String email, String firstName, String lastName, 
                       java.time.LocalDateTime createdAt, Boolean enabled, java.util.List<String> roles) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.createdAt = createdAt;
            this.enabled = enabled;
            this.roles = roles;
        }
    }

    // Inner class for event info
    public static class EventInfo {
        public Long id;
        public String title;
        public String date;
        public Integer participantCount;
        public String status;

        public EventInfo(Long id, String title, String date, Integer participantCount, String status) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.participantCount = participantCount;
            this.status = status;
        }
    }
}
