package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.dto.response.ErrorResponse;
import com.sandeep.eventrabackend.dto.response.MyRegisteredEventResponse;
import com.sandeep.eventrabackend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for authenticated user data")
public class UserController {

    private final EventService eventService;

    public UserController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/my-events")
    @Operation(
            summary = "Get the authenticated user's registered events",
            description = "Returns event registrations for the currently authenticated JWT user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registered events fetched successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MyRegisteredEventResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<MyRegisteredEventResponse>> getMyRegisteredEvents(
            Authentication authentication) {

        return ResponseEntity.ok(eventService.getRegisteredEventsForUser(authentication.getName()));
    }
}
