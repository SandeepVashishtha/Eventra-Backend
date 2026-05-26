package com.sandeep.eventrabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new event")
public class EventCreateRequest {

    @NotBlank(message = "Title is required")
    @Schema(description = "Event title", example = "Tech Conference 2026")
    private String title;

    @NotBlank(message = "Description is required")
    @Schema(description = "Detailed event description", example = "A deep dive into AI and Cloud computing.")
    private String description;

    @NotBlank(message = "Location is required")
    @Schema(description = "Physical or virtual location", example = "San Francisco, CA")
    private String location;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    @Schema(description = "Date and time when the event starts")
    private LocalDateTime eventDate;

    @Positive(message = "Capacity must be positive")
    @Schema(description = "Maximum number of attendees allowed (null for unlimited)", example = "100")
    private Integer capacity;

    @Schema(description = "Whether the event is publicly visible", defaultValue = "true")
    private Boolean isPublic;
}
