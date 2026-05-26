package com.sandeep.eventrabackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Event availability details response")
public class EventAvailabilityResponse {
    @Schema(description = "Maximum capacity of the event", example = "100")
    private Integer capacity;
    @Schema(description = "Number of users currently registered", example = "45")
    private int registeredCount;
    @Schema(description = "Remaining available spots", example = "55")
    private Integer spotsLeft;
    @Schema(description = "Indicates whether the event is fully booked", example = "false")
    private boolean isFull;
}
