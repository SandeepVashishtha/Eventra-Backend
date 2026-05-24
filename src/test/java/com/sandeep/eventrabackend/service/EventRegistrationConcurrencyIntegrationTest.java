package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.exception.EventFullException;
import com.sandeep.eventrabackend.exception.RegistrationConflictException;
import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class EventRegistrationConcurrencyIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
    }

    @Test
    void shouldPreventConcurrentOverbookingWhenCapacityIsOne() throws InterruptedException {
        int threads = 20;
        Event event = createEventWithCapacity(1);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            final long userId = i + 1L;
            executor.submit(() -> {
                try {
                    ready.countDown();
                    start.await();
                    eventService.registerForEvent(event.getId(), userId);
                    successCount.incrementAndGet();
                } catch (EventFullException | RegistrationConflictException ex) {
                    failureCount.incrementAndGet();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        done.await();
        executor.shutdown();

        Event updated = eventRepository.findById(event.getId()).orElseThrow();
        assertEquals(1, updated.getCurrentAttendees());
        assertEquals(1, successCount.get());
        assertEquals(threads - 1, failureCount.get());
    }

    private Event createEventWithCapacity(int capacity) {
        Event event = new Event();
        event.setTitle("Concurrency Test Event");
        event.setDescription("Testing optimistic locking for registrations");
        event.setLocation("Online");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setPublic(true);
        event.setMaxAttendees(capacity);
        event.setCurrentAttendees(0);
        return eventRepository.saveAndFlush(event);
    }
}
