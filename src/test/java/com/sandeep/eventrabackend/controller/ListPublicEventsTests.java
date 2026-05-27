package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.model.EventStatus;
import com.sandeep.eventrabackend.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ListPublicEventsTests {

    @Autowired MockMvc       mockMvc;
    @Autowired EventRepository eventRepository;

    // ── Fixtures ─────────────────────────────────────────────────────────────

    @BeforeEach
    void seed() {
        eventRepository.deleteAll();

        eventRepository.save(makeEvent("Spring Boot Workshop", "Tech deep-dive", "Mumbai",
                "Tech", EventStatus.UPCOMING, true,  50, future()));

        eventRepository.save(makeEvent("Jazz Night", "Live jazz performance", "Pune",
                "Music", EventStatus.UPCOMING, true,  200, future()));

        eventRepository.save(makeEvent("Private Gala", "Invitation only", "Delhi",
                "Music", EventStatus.UPCOMING, false, 100, future()));

        eventRepository.save(makeEvent("Old Tech Talk", "Past event", "Bangalore",
                "Tech", EventStatus.PAST,     true,  80, past()));

        eventRepository.save(makeEvent("Cancelled Seminar", "No longer on", "Chennai",
                "Education", EventStatus.CANCELLED, true, 40, future()));
    }

    // ── Basic listing ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/events — returns only public events")
    void returnsOnlyPublicEvents() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events", hasSize(4)))          // private event excluded
                .andExpect(jsonPath("$.totalElements", is(4)));
    }

    @Test
    @DisplayName("GET /api/events — no auth required")
    void noAuthRequired() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk());
    }

    // ── Pagination ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/events?page=0&size=2 — pagination metadata correct")
    void paginationMetadataCorrect() throws Exception {
        mockMvc.perform(get("/api/events").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage",   is(0)))
                .andExpect(jsonPath("$.pageSize",      is(2)))
                .andExpect(jsonPath("$.totalPages",    is(2)))
                .andExpect(jsonPath("$.totalElements", is(4)))
                .andExpect(jsonPath("$.events",        hasSize(2)))
                .andExpect(jsonPath("$.first",         is(true)))
                .andExpect(jsonPath("$.last",          is(false)));
    }

    @Test
    @DisplayName("GET /api/events?page=1&size=2 — second page")
    void secondPageHasTwoEvents() throws Exception {
        mockMvc.perform(get("/api/events").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage", is(1)))
                .andExpect(jsonPath("$.events",      hasSize(2)))
                .andExpect(jsonPath("$.last",        is(true)));
    }

    @Test
    @DisplayName("GET /api/events?size=200 — size is clamped to 100")
    void pageSizeClamped() throws Exception {
        mockMvc.perform(get("/api/events").param("size", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageSize", is(100)));
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/events?search=jazz — matches title")
    void searchMatchesTitle() throws Exception {
        mockMvc.perform(get("/api/events").param("search", "jazz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements",  is(1)))
                .andExpect(jsonPath("$.events[0].title", is("Jazz Night")));
    }

    @Test
    @DisplayName("GET /api/events?search=deep-dive — matches description")
    void searchMatchesDescription() throws Exception {
        mockMvc.perform(get("/api/events").param("search", "deep-dive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements",  is(1)))
                .andExpect(jsonPath("$.events[0].title", is("Spring Boot Workshop")));
    }

    @Test
    @DisplayName("GET /api/events?search=pune — matches location (case-insensitive)")
    void searchMatchesLocationCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/events").param("search", "PUNE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements",  is(1)))
                .andExpect(jsonPath("$.events[0].title", is("Jazz Night")));
    }

    @Test
    @DisplayName("GET /api/events?search=xyz — no matches returns empty page")
    void searchNoMatchReturnsEmpty() throws Exception {
        mockMvc.perform(get("/api/events").param("search", "xyz_no_match"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.events",        hasSize(0)));
    }

    // ── Status filter ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/events?status=UPCOMING — only upcoming events")
    void statusFilterUpcoming() throws Exception {
        mockMvc.perform(get("/api/events").param("status", "UPCOMING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(2)));  // Workshop + Jazz (Private excluded)
    }

    @Test
    @DisplayName("GET /api/events?status=PAST — only past events")
    void statusFilterPast() throws Exception {
        mockMvc.perform(get("/api/events").param("status", "PAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements",   is(1)))
                .andExpect(jsonPath("$.events[0].title", is("Old Tech Talk")));
    }

    @Test
    @DisplayName("GET /api/events?status=CANCELLED — only cancelled events")
    void statusFilterCancelled() throws Exception {
        mockMvc.perform(get("/api/events").param("status", "CANCELLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements",   is(1)))
                .andExpect(jsonPath("$.events[0].title", is("Cancelled Seminar")));
    }

    // ── Category filter ───────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/events?category=Tech — returns Tech events only")
    void categoryFilterTech() throws Exception {
        mockMvc.perform(get("/api/events").param("category", "Tech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(2)));  // Workshop + Old Tech Talk
    }

    @Test
    @DisplayName("GET /api/events?category=music — case-insensitive category filter")
    void categoryFilterCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/events").param("category", "music"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)));  // Jazz Night only (Private excluded)
    }

    // ── Combined filters ──────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/events?category=Tech&status=UPCOMING — combines filters")
    void combinedCategoryAndStatusFilter() throws Exception {
        mockMvc.perform(get("/api/events")
                        .param("category", "Tech")
                        .param("status",   "UPCOMING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements",   is(1)))
                .andExpect(jsonPath("$.events[0].title", is("Spring Boot Workshop")));
    }

    @Test
    @DisplayName("GET /api/events?search=workshop&category=Tech — combines search and category")
    void combinedSearchAndCategory() throws Exception {
        mockMvc.perform(get("/api/events")
                        .param("search",   "workshop")
                        .param("category", "Tech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements",   is(1)))
                .andExpect(jsonPath("$.events[0].title", is("Spring Boot Workshop")));
    }

    // ── Sorting ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/events?sort=title,asc — sorted alphabetically")
    void sortByTitleAscending() throws Exception {
        mockMvc.perform(get("/api/events").param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events[0].title", is("Cancelled Seminar")));
    }

    @Test
    @DisplayName("GET /api/events?sort=title,desc — sorted reverse alphabetically")
    void sortByTitleDescending() throws Exception {
        mockMvc.perform(get("/api/events").param("sort", "title,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events[0].title", is("Spring Boot Workshop")));
    }

    // ── Response shape ────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/events — response contains expected fields")
    void responseContainsExpectedFields() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events[0].id",              notNullValue()))
                .andExpect(jsonPath("$.events[0].title",           notNullValue()))
                .andExpect(jsonPath("$.events[0].description",     notNullValue()))
                .andExpect(jsonPath("$.events[0].location",        notNullValue()))
                .andExpect(jsonPath("$.events[0].eventDate",       notNullValue()))
                .andExpect(jsonPath("$.events[0].category",        notNullValue()))
                .andExpect(jsonPath("$.events[0].status",          notNullValue()))
                .andExpect(jsonPath("$.events[0].registeredCount", notNullValue()));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Event makeEvent(String title, String description, String location,
                            String category, EventStatus status, boolean isPublic,
                            int capacity, LocalDateTime eventDate) {
        Event e = new Event();
        e.setTitle(title);
        e.setDescription(description);
        e.setLocation(location);
        e.setCategory(category);
        e.setStatus(status);
        e.setPublic(isPublic);
        e.setCapacity(capacity);
        e.setEventDate(eventDate);
        return e;
    }

    private LocalDateTime future() { return LocalDateTime.now().plusDays(30); }
    private LocalDateTime past()   { return LocalDateTime.now().minusDays(30); }
}