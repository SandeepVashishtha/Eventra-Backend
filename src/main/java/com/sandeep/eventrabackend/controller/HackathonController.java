package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.dto.response.HackathonResponse;
import com.sandeep.eventrabackend.service.HackathonService;
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
@RequestMapping("/api/hackathons")
@Tag(name = "Hackathons", description = "Endpoints for viewing and interacting with hackathons")
public class HackathonController {

    private final HackathonService hackathonService;

    public HackathonController(HackathonService hackathonService) {
        this.hackathonService = hackathonService;
    }

    @GetMapping
    @Operation(
            summary = "Get all hackathons",
            description = "Returns a list of all available hackathons."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Hackathons fetched successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = HackathonResponse.class))
                    )
            )
    })
    public ResponseEntity<List<HackathonResponse>> getAllHackathons() {
        return ResponseEntity.ok(hackathonService.getAllHackathons());
    }
}
