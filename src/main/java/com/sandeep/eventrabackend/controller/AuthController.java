package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.dto.request.LoginRequest;
import com.sandeep.eventrabackend.dto.request.SignupRequest;
import com.sandeep.eventrabackend.dto.response.AuthResponse;
import com.sandeep.eventrabackend.dto.response.ErrorResponse;
import com.sandeep.eventrabackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login endpoints for Eventra users")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ─── SIGNUP ─────────────────────────────────────────────────────────────────

    @PostMapping("/signup")
    @SecurityRequirements   // no auth needed for this endpoint
    @Operation(
            summary = "Register a new user account",
            description = """
                    Creates a new Eventra user account.
                    
                    **Fields required:**
                    - `firstName`, `lastName` — 2–50 characters each
                    - `email` — valid email, must be unique
                    - `password` — minimum 8 characters
                    - `confirmPassword` — must exactly match `password`
                    
                    On success returns a **JWT token** that can be used immediately.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or passwords don't match",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already registered",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─── LOGIN ──────────────────────────────────────────────────────────────────

    @PostMapping("/login")
    @SecurityRequirements   // no auth needed for this endpoint
    @Operation(
            summary = "Login with username/email and password",
            description = """
                    Authenticates an existing user and returns a JWT token.
                    
                    **Accepts either:**
                    - Email address  (e.g. `john@example.com`)
                    - Username       (e.g. `john_doe`)
                    
                    Use the returned `token` as `Authorization: Bearer <token>` for protected endpoints.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error — missing fields",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
