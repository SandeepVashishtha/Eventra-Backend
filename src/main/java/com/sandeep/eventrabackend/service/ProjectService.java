package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.dto.response.ProjectResponse;
import com.sandeep.eventrabackend.exception.ProjectNotFoundException;
import com.sandeep.eventrabackend.model.Project;
import com.sandeep.eventrabackend.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::toProjectResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(this::toProjectResponse)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
    }

    private ProjectResponse toProjectResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .category(project.getCategory())
                .thumbnailUrl(project.getThumbnailUrl())
                .githubUrl(project.getGithubUrl())
                .upvotes(project.getUpvotes())
                .build();
    }
}
