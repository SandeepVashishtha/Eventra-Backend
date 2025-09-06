package com.eventra.controller;

import com.eventra.exception.UnauthorizedAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        log.info("Received request to get user profile");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getName())) {
            log.warn("Unauthorized access attempt to user profile");
            throw new UnauthorizedAccessException("Authentication required to access user profile");
        }
        
        String email = authentication.getName();
        log.info("Retrieved profile for user: {}", email);
        
        // Create the response with user profile data
        UserProfileResponse response = new UserProfileResponse();
        response.setEmail(email);
        
        // TODO: Fetch actual user data from database using UserService
        // For now, using placeholder data - you should replace this with actual database lookup
        response.setFirstName("John"); // Replace with actual data from database
        response.setLastName("Doe");   // Replace with actual data from database
        response.setProfilePicture(null); // Will be null initially until user uploads
        response.setRoles(new String[]{"USER"}); // Add user roles
        response.setPermissions(new String[]{"CREATE_EVENT"}); // Add user permissions
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events")
    public ResponseEntity<java.util.List<UserEventInfo>> getUserEvents() {
        // For now, return sample data since we don't have Event entity yet
        java.util.List<UserEventInfo> events = java.util.List.of(
            new UserEventInfo(1L, "My Event 1", "2025-08-10", "A sample event I'm attending"),
            new UserEventInfo(2L, "My Event 2", "2025-08-12", "Another event I'm registered for")
        );
        return ResponseEntity.ok(events);
    }

    // User Profile Response DTO
    public static class UserProfileResponse {
        private String email;
        private String firstName;
        private String lastName;
        private String profilePicture;
        private String[] roles;
        private String[] permissions;
        
        // Default constructor
        public UserProfileResponse() {}
        
        // Constructor with all fields
        public UserProfileResponse(String email, String firstName, String lastName, 
                                 String profilePicture, String[] roles, String[] permissions) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.profilePicture = profilePicture;
            this.roles = roles;
            this.permissions = permissions;
        }
        
        // Getters and Setters
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public String getProfilePicture() {
            return profilePicture;
        }
        
        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }
        
        public String[] getRoles() {
            return roles;
        }
        
        public void setRoles(String[] roles) {
            this.roles = roles;
        }
        
        public String[] getPermissions() {
            return permissions;
        }
        
        public void setPermissions(String[] permissions) {
            this.permissions = permissions;
        }
        
        // Helper method to get full name
        public String getFullName() {
            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            }
            return email;
        }
    }

    // Inner class for user event info (keeping your existing implementation)
    public static class UserEventInfo {
        public Long id;
        public String title;
        public String date;
        public String description;

        public UserEventInfo(Long id, String title, String date, String description) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.description = description;
        }
    }
}
