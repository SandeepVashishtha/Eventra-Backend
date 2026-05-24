package com.sandeep.eventrabackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventAvailabilityResponse {
    private Integer capacity;
    private int registeredCount;
    private Integer spotsLeft;
    private boolean isFull;
}
