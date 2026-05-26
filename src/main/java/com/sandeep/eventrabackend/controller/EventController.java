package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.dto.response.EventAvailabilityResponse;
import com.sandeep.eventrabackend.dto.response.RegistrationResponse;
import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Endpoints for managing and interacting with events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // ── Issue #2101 — GET /api/events/{id} ──────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Get a public event by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found or not public")
    })
    public ResponseEntity<Event> getPublicEventById(@PathVariable Long id) {
        Event event = eventService.getPublicEventById(id);
        return ResponseEntity.ok(event);
    }

    // ── Issue #2101 — GET /api/events/{id}/availability ─────────────────────

    @GetMapping("/{id}/availability")
    @Operation(
            summary = "Get availability (spots left) for an event",
            description = "Public endpoint — no authentication required. " +
                    "Returns capacity, current registrations, spots left, and whether the event has passed. " +
                    "The 'eventPassed' flag should be used by the frontend to show a 'This event has already passed' notice."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Availability data returned"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventAvailabilityResponse> getEventAvailability(@PathVariable Long id) {
        EventAvailabilityResponse response = eventService.getEventAvailability(id);
        return ResponseEntity.ok(response);
    }

    // ── Issue #2102 — POST /api/events/{id}/register ─────────────────────────

    @PostMapping("/{id}/register")
    @Operation(
            summary = "Register the currently authenticated user for an event",
            description = "JWT authentication required. " +
                    "Returns 409 if the event is full or the user is already registered.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration confirmed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — JWT required"),
            @ApiResponse(responseCode = "404", description = "Event or user not found"),
            @ApiResponse(responseCode = "409", description = "Event full or user already registered")
    })
    public ResponseEntity<RegistrationResponse> registerForEvent(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();
        RegistrationResponse response =
                eventService.registerUserForEvent(id, userEmail);

        return ResponseEntity.ok(response);
    }
}