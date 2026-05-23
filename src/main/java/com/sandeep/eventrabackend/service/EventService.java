package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.dto.response.EventAvailabilityResponse;
import com.sandeep.eventrabackend.exception.EventFullException;
import com.sandeep.eventrabackend.exception.EventNotFoundException;
import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.model.User;
import com.sandeep.eventrabackend.repository.EventRepository;
import com.sandeep.eventrabackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public Event getPublicEventById(long id) {
        return eventRepository.findByIdAndIsPublicTrue(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found or is not public with id: " + id));
    }

    @Transactional
    public Event registerUserForEvent(Long eventId, String userEmail) {
        return executeRegistration(eventId, userEmail);
    }

    private Event executeRegistration(Long eventId, String userEmail) {
        // Use pessimistic lock to ensure thread safety and prevent overbooking
        Event event = eventRepository.findByIdWithLock(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        // If user is already registered, follow existing behavior (just return the event)
        if (event.getAttendees().contains(user)) {
            return event;
        }

        // Check capacity
        if (event.getCapacity() != null && event.getRegisteredCount() >= event.getCapacity()) {
            throw new EventFullException("Event is already full. Capacity: " + event.getCapacity());
        }

        // Increment count and add user
        event.getAttendees().add(user);
        event.setRegisteredCount(event.getAttendees().size());

        return eventRepository.save(event);
    }

    public EventAvailabilityResponse getEventAvailability(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        Integer capacity = event.getCapacity();
        int registeredCount = event.getRegisteredCount();
        Integer spotsLeft = (capacity == null) ? null : Math.max(0, capacity - registeredCount);
        boolean isFull = (capacity != null) && (registeredCount >= capacity);

        return EventAvailabilityResponse.builder()
                .capacity(capacity)
                .registeredCount(registeredCount)
                .spotsLeft(spotsLeft)
                .isFull(isFull)
                .build();
    }
}
