package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.repository.EventRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Optional<Event> getPublicEventById(Long id) {
        return eventRepository.findByIdAndIsPublicTrue(id);
    }
}
