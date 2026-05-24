package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.model.Role;
import com.sandeep.eventrabackend.model.User;
import com.sandeep.eventrabackend.repository.EventRepository;
import com.sandeep.eventrabackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventRegistrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    private Long eventId;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        userRepository.deleteAll();

        Event event = new Event();
        event.setTitle("Test Event");
        event.setCapacity(5);
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event = eventRepository.save(event);
        eventId = event.getId();

        for (int i = 1; i <= 10; i++) {
            User user = User.builder()
                    .firstName("User" + i)
                    .lastName("Test")
                    .email("user" + i + "@example.com")
                    .username("user" + i)
                    .password("password")
                    .role(Role.CLIENT)
                    .build();
            userRepository.save(user);
        }
    }

    @Test
    void testConcurrentRegistration() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        for (int i = 1; i <= threadCount; i++) {
            final String email = "user" + i + "@example.com";
            executorService.execute(() -> {
                try {
                    latch.await();
                    int status = mockMvc.perform(post("/api/events/" + eventId + "/register")
                            .with(user(email)))
                            .andReturn().getResponse().getStatus();
                    
                    if (status == 200) {
                        successCount.incrementAndGet();
                    } else if (status == 409) {
                        conflictCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        latch.countDown();
        finishLatch.await();
        executorService.shutdown();

        assertEquals(5, successCount.get(), "Should have exactly 5 successful registrations");
        
        Event event = eventRepository.findById(eventId).orElseThrow();
        assertEquals(5, event.getRegisteredCount(), "Final registeredCount should be 5");
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    void testAvailabilityEndpoint() throws Exception {
        mockMvc.perform(get("/api/events/" + eventId + "/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(5))
                .andExpect(jsonPath("$.registeredCount").value(0))
                .andExpect(jsonPath("$.spotsLeft").value(5))
                .andExpect(jsonPath("$.full").value(false));
    }
}
