package com.eventra.service;

import com.eventra.dto.AuthResponse;
import com.eventra.dto.LoginRequest;
import com.eventra.dto.MessageResponse;
import com.eventra.dto.SignupRequest;
import com.eventra.entity.User;
import com.eventra.entity.Role;
import com.eventra.exception.InvalidCredentialsException;
import com.eventra.exception.UserAlreadyExistsException;
import com.eventra.exception.UserNotFoundException;
import com.eventra.repository.UserRepository;
import com.eventra.repository.RoleRepository;
import com.eventra.util.JwtUtil;
import com.eventra.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public MessageResponse signup(SignupRequest signupRequest) {
        log.info("Attempting to register user with email: {}", signupRequest.getEmail());
        
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            log.warn("Registration failed: Email {} already exists", signupRequest.getEmail());
            throw new UserAlreadyExistsException("Email already exists!");
        }

        // Additional password complexity validation
        if (!ValidationUtil.isValidPassword(signupRequest.getPassword())) {
            log.warn("Registration failed: Password does not meet complexity requirements for email: {}", signupRequest.getEmail());
            throw new RuntimeException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)");
        }

        try {
            User user = new User();
            user.setFirstName(signupRequest.getFirstName());
            user.setLastName(signupRequest.getLastName());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            user.setEnabled(true);
            
            // Assign role based on selection or default to USER
            Role.RoleName selectedRole = Role.RoleName.USER; // Default to USER role
            
            // If role is provided in signup request, validate and use it
            if (signupRequest.getRole() != null && !signupRequest.getRole().isEmpty()) {
                try {
                    selectedRole = Role.RoleName.valueOf(signupRequest.getRole().toUpperCase());
                    // Only allow ADMIN and USER roles for signup
                    if (selectedRole != Role.RoleName.ADMIN && selectedRole != Role.RoleName.USER) {
                        selectedRole = Role.RoleName.USER; // Fall back to USER if invalid role
                        log.warn("Invalid role '{}' provided, defaulting to USER role", signupRequest.getRole());
                    }
                } catch (IllegalArgumentException e) {
                    selectedRole = Role.RoleName.USER; // Fall back to USER if invalid role
                    log.warn("Invalid role '{}' provided, defaulting to USER role", signupRequest.getRole());
                }
            }
            
            // Try to assign role, but don't fail if role system isn't fully set up yet
            try {
                log.info("Attempting to assign role: {}", selectedRole);
                Role role = roleRepository.findByName(selectedRole).orElse(null);
                if (role != null) {
                    Set<Role> roles = new HashSet<>();
                    roles.add(role);
                    user.setRoles(roles);
                    log.info("Successfully assigned role {} to user {}", selectedRole, user.getEmail());
                } else {
                    log.warn("Role {} not found in database, user will have no roles", selectedRole);
                }
            } catch (Exception e) {
                log.warn("Role assignment failed, proceeding without roles: {}", e.getMessage(), e);
                // Continue without roles if role system isn't fully set up
            }

            userRepository.save(user);
            log.info("User registered successfully: {}", signupRequest.getEmail());
            return new MessageResponse("User registered successfully!");
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register user. Please try again.");
        }
    }
    
    public MessageResponse createAdminUser(String email, String password, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists!");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        
        // Assign ADMIN role
        Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
            .orElseThrow(() -> new RuntimeException("Admin role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        user.setRoles(roles);

        userRepository.save(user);
        return new MessageResponse("Admin user created successfully!");
    }

    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Attempting login for user: {}", loginRequest.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

            // Extract roles and permissions with fallback for incomplete role system
            Set<String> roles = new HashSet<>();
            Set<String> permissions = new HashSet<>();
            
            try {
                if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                    roles = user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(java.util.stream.Collectors.toSet());
                        
                    permissions = user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(permission -> permission.getName().name())
                        .collect(java.util.stream.Collectors.toSet());
                }
            } catch (Exception e) {
                log.warn("Failed to extract roles/permissions, using empty sets: {}", e.getMessage());
                // roles and permissions already initialized as empty sets
            }

            log.info("User logged in successfully: {}", loginRequest.getEmail());
            return new AuthResponse(jwt, user.getEmail(), user.getFirstName(), user.getLastName(), 
                                   user.getId(), roles, permissions);
            
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user {}: Invalid credentials", loginRequest.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (UserNotFoundException e) {
            log.warn("Login failed for user {}: User not found", loginRequest.getEmail());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage(), e);
            throw new RuntimeException("Login failed. Please try again.");
        }
    }
}
