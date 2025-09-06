package com.eventra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProjectSubmission {
    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    private String description;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank
    private String category;

    private List<String> techStack;

    @Size(max = 512)
    private String githubUrl;

    @Size(max = 512)
    private String liveDemo;

    @Size(max = 512)
    private String image;

    @Size(max = 50)
    private String difficulty;

    private Integer openIssues;
    private Integer pullRequests;
    private Integer stars;
    private Integer forks;
}