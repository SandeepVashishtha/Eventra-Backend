package com.eventra.controller;

import com.eventra.dto.EventDTO;
import com.eventra.entity.Event;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import com.eventra.repository.EventsRepository;
import com.eventra.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService; // mocking the service

    @Test
    void testCreateEvent() throws Exception {
        String eventJson = """
            {
              "date": "2025-10-05",
              "description": "A tech conference on AI",
              "title": "AI Summit 2025",
              "status": "Scheduled",
              "category": "Technology",
              "capacity": 200,
              "organizer": "Eventra Team",
              "location": "Bangalore"
            }
        """;

        Event mockEvent = new Event();
        mockEvent.setTitle("AI Summit 2025");
        mockEvent.setLocation("Bangalore");
        mockEvent.setCapacity(200);

        when(eventService.createEvent(any(EventDTO.class))).thenReturn(mockEvent);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("AI Summit 2025"))
                .andExpect(jsonPath("$.location").value("Bangalore"))
                .andExpect(jsonPath("$.capacity").value(200));
    }
}
