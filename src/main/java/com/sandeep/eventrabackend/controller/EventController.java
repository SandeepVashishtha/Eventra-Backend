package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.dto.response.EventAvailabilityResponse;
import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.sandeep.eventrabackend.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Endpoints for managing and interacting with events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get a public event by ID",
            description = "Fetches a public event using its unique event ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event fetched successfully",
                    content = @Content(schema = @Schema(implementation = Event.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found or not public",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Event> getPublicEventById(@Parameter(description = "ID of the public event") @PathVariable Long id) {
        Event event = eventService.getPublicEventById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/{id}/register")
    @Operation(
            summary = "Register the authenticated user for an event",
            description = "Registers the currently logged-in user for a specific event."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully registered for event",
                    content = @Content(schema = @Schema(implementation = Event.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Event is already full",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Event> registerForEvent(@Parameter(description = "ID of the event to register for")
                                                      @PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        Event event = eventService.registerUserForEvent(id, userEmail);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/{id}/availability")
    @Operation(
            summary = "Get event availability",
            description = "Returns capacity, registered users count, and remaining spots for an event."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event availability fetched successfully",
                    content = @Content(schema = @Schema(implementation = EventAvailabilityResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EventAvailabilityResponse> getEventAvailability(@Parameter(description = "ID of the event")
                                                                              @PathVariable Long id) {
        EventAvailabilityResponse response = eventService.getEventAvailability(id);
        return ResponseEntity.ok(response);
    }
}
