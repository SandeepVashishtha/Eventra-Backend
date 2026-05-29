package com.sandeep.eventrabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProjectControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCategories_PublicAccess_Returns200() throws Exception {
        mockMvc.perform(get("/api/projects/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(8)))
                .andExpect(jsonPath("$", hasItems(
                        "Mobile Development",
                        "Web Development",
                        "Developer Tools",
                        "Machine Learning",
                        "DevOps",
                        "Design",
                        "IoT",
                        "Blockchain"
                )));
    }
}
