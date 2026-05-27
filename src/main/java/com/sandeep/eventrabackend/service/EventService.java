package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.dto.response.EventAvailabilityResponse;
import com.sandeep.eventrabackend.dto.response.RegistrationResponse;
import com.sandeep.eventrabackend.exception.EventFullException;
import com.sandeep.eventrabackend.exception.EventNotFoundException;
import com.sandeep.eventrabackend.exception.RegistrationConflictException;
import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.model.User;
import com.sandeep.eventrabackend.repository.EventRepository;
import com.sandeep.eventrabackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service handling event queries and registrations.
 *
 * <h3>Concurrency strategy (Issue #2104)</h3>
 * <ul>
 *   <li><b>Pessimistic write lock</b> ({@code SELECT … FOR UPDATE}) is acquired
 *       via {@link EventRepository#findByIdWithLock} at the start of every
 *       registration transaction. Only one thread can hold the lock at a time,
 *       so the capacity check and the attendee-set mutation are serialised.</li>
 *   <li><b>Optimistic version field</b> ({@code @Version} on {@link Event}) acts
 *       as a safety net: if two transactions somehow both pass the lock path and
 *       attempt to commit, JPA will reject the second with an
 *       {@link ObjectOptimisticLockingFailureException}, which the
 *       {@code GlobalExceptionHandler} converts to HTTP 409.</li>
 *   <li>A <b>retry loop</b> (max {@value #MAX_REGISTRATION_RETRIES} attempts)
 *       transparently re-tries on optimistic conflicts so transient contention
 *       does not surface as an error to the caller.</li>
 * </ul>
 */
@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    /** Maximum number of automatic retries on optimistic-lock conflict. */
    private static final int MAX_REGISTRATION_RETRIES = 3;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    // ── Issue #2101 — Event Availability Check ───────────────────────────────

    /**
     * Returns availability data for the given event.
     * The endpoint is public (no JWT required) so anyone can check spots.
     * The {@code eventPassed} flag in the response lets the frontend display
     * a "This event has already passed" notice instead of a registration button.
     *
     * @throws EventNotFoundException if no event with {@code id} exists
     */
    public EventAvailabilityResponse getEventAvailability(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found with id: " + id));

        Integer capacity = event.getCapacity();
        int registeredCount = event.getRegisteredCount();

        Integer spotsLeft =
                (capacity == null)
                        ? null
                        : Math.max(0, capacity - registeredCount);

        boolean isFull =
                (capacity != null) && (registeredCount >= capacity);

        return EventAvailabilityResponse.builder()
                .capacity(capacity)
                .registeredCount(registeredCount)
                .spotsLeft(spotsLeft)
                .isFull(isFull)
                .eventPassed(event.isEventPast())
                .build();
    }

    // ── Issue #2102 — Public Event Fetch ─────────────────────────────────────

    /**
     * Retrieves a public event by ID.
     *
     * @throws EventNotFoundException if the event does not exist or is not public
     */
    public Event getPublicEventById(long id) {
        return eventRepository.findByIdAndIsPublicTrue(id)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                "Event not found or is not public with id: " + id));
    }

    /**
     * Registers the authenticated user for an event.
     *
     * <p>Business rules enforced:
     * <ol>
     *   <li>Event must exist → 404</li>
     *   <li>User must exist (resolved from JWT email) → 404</li>
     *   <li>User must not already be registered → 409</li>
     *   <li>Event must not be at capacity → 409</li>
     * </ol>
     *
     * @param eventId ID of the event to register for
     * @param userEmail email extracted from JWT principal
     * @return registration confirmation response
     */
    @Transactional
    public RegistrationResponse registerUserForEvent(Long eventId, String userEmail) {

        ObjectOptimisticLockingFailureException lastConflict = null;

        for (int attempt = 1; attempt <= MAX_REGISTRATION_RETRIES; attempt++) {
            try {
                return executeRegistration(eventId, userEmail);

            } catch (ObjectOptimisticLockingFailureException ex) {
                lastConflict = ex;

                log.warn(
                        "Optimistic lock conflict on event {} (attempt {}/{})",
                        eventId,
                        attempt,
                        MAX_REGISTRATION_RETRIES
                );
            }
        }

        log.error(
                "Registration failed after {} retries for event {} by {}",
                MAX_REGISTRATION_RETRIES,
                eventId,
                userEmail
        );

        throw new RegistrationConflictException(
                "Registration could not be completed due to high demand. Please try again.");
    }

    // ── Private Helpers ──────────────────────────────────────────────────────

    private RegistrationResponse executeRegistration(
            Long eventId,
            String userEmail) {

        Event event = eventRepository.findByIdWithLock(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                "Event not found with id: " + eventId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + userEmail));

        // Check unique user-event registration by matching user IDs inside the attendees list
        boolean isAlreadyRegistered = event.getAttendees().stream()
        .anyMatch(attendee -> attendee.getId().equals(user.getId()));

        if (isAlreadyRegistered) {
                throw new RegistrationConflictException(
                        "You are already registered for this event.");
        }

        if (event.getCapacity() != null
                && event.getRegisteredCount() >= event.getCapacity()) {

            throw new EventFullException(
                    "Event is already full. Capacity: " + event.getCapacity());
        }

        event.getAttendees().add(user);
        event.setRegisteredCount(event.getAttendees().size());

        Event saved = eventRepository.save(event);

        Integer spotsRemaining =
                (saved.getCapacity() == null)
                        ? null
                        : Math.max(
                                0,
                                saved.getCapacity() - saved.getRegisteredCount());

        return RegistrationResponse.builder()
                .eventId(saved.getId())
                .eventTitle(saved.getTitle())
                .userEmail(userEmail)
                .registeredAt(LocalDateTime.now())
                .spotsRemaining(spotsRemaining)
                .registrationStatus("CONFIRMED")
                .build();
    }
}