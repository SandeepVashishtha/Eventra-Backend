package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.model.EventRegistration;
import com.sandeep.eventrabackend.model.Hackathon;
import com.sandeep.eventrabackend.model.HackathonRegistration;
import com.sandeep.eventrabackend.model.Role;
import com.sandeep.eventrabackend.model.User;
import com.sandeep.eventrabackend.repository.EventRegistrationRepository;
import com.sandeep.eventrabackend.repository.EventRepository;
import com.sandeep.eventrabackend.repository.HackathonRegistrationRepository;
import com.sandeep.eventrabackend.repository.HackathonRepository;
import com.sandeep.eventrabackend.repository.NotificationRepository;
import com.sandeep.eventrabackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Regression tests for issue #71: Admin delete event/hackathon must not throw
 * a foreign-key violation when registrations exist.
 *
 * <p>These hit the <em>admin</em> delete endpoints ({@code /api/admin/events/{id}}
 * and {@code /api/admin/hackathons/{id}}) which delegate to
 * {@code AdminService.deleteEvent}/{@code deleteHackathon}. Before the fix those
 * methods called {@code deleteById} directly without removing registrations,
 * causing a {@code DataIntegrityViolationException} (HTTP 500) whenever the
 * entity had at least one registration.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminDeleteTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private HackathonRepository hackathonRepository;

    @Autowired
    private HackathonRegistrationRepository hackathonRegistrationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Event eventWithRegistration;
    private Hackathon hackathonWithRegistration;
    private User client;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        hackathonRegistrationRepository.deleteAll();
        eventRegistrationRepository.deleteAll();
        eventRepository.deleteAll();
        hackathonRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .role(Role.ADMIN)
                .build());

        client = userRepository.save(User.builder()
                .firstName("Client")
                .lastName("User")
                .email("client@example.com")
                .username("client")
                .password(passwordEncoder.encode("password"))
                .role(Role.CLIENT)
                .build());

        Event event = new Event();
        event.setTitle("Admin-deletable event");
        event.setDescription("Description");
        event.setLocation("Online");
        event.setEventDate(LocalDateTime.now().plusDays(5));
        event.setCapacity(100);
        event.setPublic(true);
        eventWithRegistration = eventRepository.save(event);

        EventRegistration reg = new EventRegistration();
        reg.setEvent(eventWithRegistration);
        reg.setUser(client);
        reg.setStatus("CONFIRMED");
        eventRegistrationRepository.save(reg);

        hackathonWithRegistration = hackathonRepository.save(Hackathon.builder()
                .title("Admin-deletable hackathon")
                .description("Description")
                .organizer("Org")
                .startDate(LocalDateTime.now().plusDays(5))
                .endDate(LocalDateTime.now().plusDays(7))
                .location("Online")
                .mode("ONLINE")
                .build());

        HackathonRegistration hreg = HackathonRegistration.builder()
                .hackathon(hackathonWithRegistration)
                .user(client)
                .status("CONFIRMED")
                .build();
        hackathonRegistrationRepository.save(hreg);
    }

    @Test
    @DisplayName("Admin delete event with registrations returns 204 and cleans up registrations")
    void adminDeleteEvent_WithRegistrations_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/events/" + eventWithRegistration.getId())
                        .with(user("admin@example.com")
                                .authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isNoContent());

        assertFalse(eventRepository.existsById(eventWithRegistration.getId()));
        assertFalse(eventRegistrationRepository
                .existsByEvent_IdAndUser_Email(eventWithRegistration.getId(), client.getEmail()));
    }

    @Test
    @DisplayName("Admin delete hackathon with registrations returns 204 and cleans up registrations")
    void adminDeleteHackathon_WithRegistrations_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/hackathons/" + hackathonWithRegistration.getId())
                        .with(user("admin@example.com")
                                .authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isNoContent());

        assertFalse(hackathonRepository.existsById(hackathonWithRegistration.getId()));
        assertFalse(hackathonRegistrationRepository
                .existsByHackathon_IdAndUser_Email(hackathonWithRegistration.getId(), client.getEmail()));
    }

    @Test
    @DisplayName("Admin delete non-existent event returns 404")
    void adminDeleteEvent_NonExistent_NotFound() throws Exception {
        mockMvc.perform(delete("/api/admin/events/999999")
                        .with(user("admin@example.com")
                                .authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Admin delete non-existent hackathon returns 404")
    void adminDeleteHackathon_NonExistent_NotFound() throws Exception {
        mockMvc.perform(delete("/api/admin/hackathons/999999")
                        .with(user("admin@example.com")
                                .authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isNotFound());
    }
}
