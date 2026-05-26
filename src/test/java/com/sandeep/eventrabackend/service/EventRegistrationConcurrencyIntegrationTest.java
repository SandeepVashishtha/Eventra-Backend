package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.exception.EventFullException;
import com.sandeep.eventrabackend.exception.RegistrationConflictException;
import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.model.Role;
import com.sandeep.eventrabackend.model.User;
import com.sandeep.eventrabackend.repository.EventRepository;
import com.sandeep.eventrabackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldPreventConcurrentOverbookingWhenCapacityIsOne() throws InterruptedException {
        int threads = 20;

        List<String> emails = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            String email = "testuser" + i + "@test.com";
            userRepository.save(User.builder()
                    .firstName("Test")
                    .lastName("User" + i)
                    .email(email)
                    .username("testuser" + i)
                    .password("password")
                    .role(Role.CLIENT)
                    .build());
            emails.add(email);
        }

        Event event = createEventWithCapacity(1);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            final String email = emails.get(i);
            executor.submit(() -> {
                try {
                    ready.countDown();
                    start.await();
                    eventService.registerUserForEvent(event.getId(), email);
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
        assertEquals(1, updated.getRegisteredCount());
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
        event.setCapacity(capacity);
        event.setRegisteredCount(0);
        return eventRepository.saveAndFlush(event);
    }
}
