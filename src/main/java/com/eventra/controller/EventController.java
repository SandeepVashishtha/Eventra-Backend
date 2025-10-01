package com.eventra.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;   // ✅ correct import

import com.eventra.dto.EventDTO;
import com.eventra.entity.Event;
import com.eventra.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired 
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDto) {
 try {
        System.out.println("eventService = " + eventService);
        Event savedEvent = eventService.createEvent(eventDto);
        return ResponseEntity.ok(savedEvent);
    } catch (Exception e) {
        e.printStackTrace(); // <--- this will show the real exception
        throw e; // rethrow so test still fails
    }
    }
}
