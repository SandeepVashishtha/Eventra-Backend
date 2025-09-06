package com.eventra.controller;

import com.eventra.dto.AuthResponse;
import com.eventra.dto.LoginRequest;
import com.eventra.dto.MessageResponse;
import com.eventra.dto.SignupRequest;
import com.eventra.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or email already exists",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))
        )
    })
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("Received signup request for email: {}", signupRequest.getEmail());
        MessageResponse response = authService.signup(signupRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates user credentials and returns JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Authentication successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))
        )
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Received login request for email: {}", loginRequest.getEmail());
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Login failed for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Login failed: " + e.getMessage()));
        }
    }
}
