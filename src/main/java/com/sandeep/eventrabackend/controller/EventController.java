package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.dto.response.EventAvailabilityResponse;
import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
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


    @GetMapping("/{id}")
    @Operation(summary = "Get a public event by ID")
    public ResponseEntity<Event> getPublicEventById(@PathVariable Long id) {
        Event event = eventService.getPublicEventById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/{id}/register")
    @Operation(summary = "Register the currently authenticated user for an event")
    public ResponseEntity<Event> registerForEvent(@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        Event event = eventService.registerUserForEvent(id, userEmail);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Get the availability (spots left) for an event")
    public ResponseEntity<EventAvailabilityResponse> getEventAvailability(@PathVariable Long id) {
        EventAvailabilityResponse response = eventService.getEventAvailability(id);
        return ResponseEntity.ok(response);
    }
}
