package com.sandeep.eventrabackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Endpoints for managing and interacting with projects")
public class ProjectController {

    private static final List<String> CATEGORIES = List.of(
            "Mobile Development",
            "Web Development",
            "Developer Tools",
            "Machine Learning",
            "DevOps",
            "Design",
            "IoT",
            "Blockchain"
    );

    @GetMapping("/categories")
    @Operation(
            summary = "Get all project categories",
            description = "Returns a list of available categories for projects."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories fetched successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )
            )
    })
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(CATEGORIES);
    }
}
