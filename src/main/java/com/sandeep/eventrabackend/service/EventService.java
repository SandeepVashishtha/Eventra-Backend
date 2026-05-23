package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.exception.EventFullException;
import com.sandeep.eventrabackend.exception.EventNotFoundException;
import com.sandeep.eventrabackend.exception.RegistrationConflictException;
import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.repository.EventRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    @Transactional
    public void registerForEvent(Long eventId, Long userId) {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

            if (event.getCurrentAttendees() >= event.getMaxAttendees()) {
                throw new EventFullException("This event has reached capacity.");
            }

            event.setCurrentAttendees(event.getCurrentAttendees() + 1);
            eventRepository.saveAndFlush(event);
        } catch (OptimisticLockingFailureException ex) {
            throw new RegistrationConflictException(
                    "Too many simultaneous registrations. Please try again."
            );
        }
    }
}
