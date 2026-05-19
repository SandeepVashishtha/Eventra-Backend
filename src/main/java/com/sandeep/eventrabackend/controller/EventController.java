package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Event> getPublicEventById(@PathVariable Long id) {
        Event event = eventService.getPublicEventById(id);
        return ResponseEntity.ok(event);
    }
}
