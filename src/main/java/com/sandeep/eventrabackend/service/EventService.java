package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.exception.EventNotFoundException;
import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.repository.EventRepository;
import org.springframework.stereotype.Service;


@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event getPublicEventById(long id) {
        return eventRepository.findByIdAndIsPublicTrue(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found or is not public with id: " + id));
    }
}
