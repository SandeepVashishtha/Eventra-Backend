package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.model.Project;
import com.sandeep.eventrabackend.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
    }

    @Test
    void testGetAllProjects_EmptyList_Returns200() throws Exception {
        mockMvc.perform(get("/api/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetAllProjects_PublicAccess_ReturnsProjects() throws Exception {
        Project project = Project.builder()
                .title("Test Project")
                .description("Test Description")
                .category("Web Development")
                .thumbnailUrl("http://example.com/thumb.png")
                .githubUrl("http://github.com/test/repo")
                .upvotes(10)
                .build();
        projectRepository.save(project);

        mockMvc.perform(get("/api/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Test Project"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].category").value("Web Development"))
                .andExpect(jsonPath("$[0].thumbnailUrl").value("http://example.com/thumb.png"))
                .andExpect(jsonPath("$[0].githubUrl").value("http://github.com/test/repo"))
                .andExpect(jsonPath("$[0].upvotes").value(10));
    }

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
