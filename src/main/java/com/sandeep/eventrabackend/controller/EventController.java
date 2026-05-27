package com.sandeep.eventrabackend.controller;

import com.sandeep.eventrabackend.dto.response.ErrorResponse;
import com.sandeep.eventrabackend.dto.response.EventAvailabilityResponse;
import com.sandeep.eventrabackend.dto.response.EventListResponse;
import com.sandeep.eventrabackend.dto.response.RegistrationResponse;
import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.model.EventStatus;
import com.sandeep.eventrabackend.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/events")
@Tag(
        name = "Events",
        description = "Endpoints for managing and interacting with events"
)
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // ── Issue #2096 — GET /api/events ──────────────────────────────────

    @GetMapping
    @Operation(
            summary = "List public events",
            description = """
                    Returns a paginated list of public events.
 
                    **Filtering**
                    - `search`   — case-insensitive substring match on title, description, and location
                    - `status`   — exact match on `UPCOMING`, `ONGOING`, `PAST`, or `CANCELLED`
                    - `category` — case-insensitive exact match on category (e.g. `Music`, `Tech`)
 
                    **Pagination**
                    - `page` (0-indexed, default 0)
                    - `size` (default 10, max 100)
                    - `sort` — field name + direction, e.g. `eventDate,asc` (default)
 
                    No authentication required.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Events returned successfully",
                    content = @Content(schema = @Schema(implementation = EventListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameter (e.g. unknown status value)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EventListResponse> listPublicEvents(

            @Parameter(description = "Free-text search across title, description, and location")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by event status: UPCOMING | ONGOING | PAST | CANCELLED")
            @RequestParam(required = false) EventStatus status,

            @Parameter(description = "Filter by category (case-insensitive), e.g. 'Music'")
            @RequestParam(required = false) String category,

            @Parameter(description = "0-indexed page number (default: 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size between 1 and 100 (default: 10)")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort field and direction, e.g. eventDate,asc (default)")
            @RequestParam(defaultValue = "eventDate,asc") String sort
    ) {
        int clampedSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = buildPageable(page, clampedSize, sort);

        EventListResponse response =
                eventService.getPublicEvents(search, status, category, pageable);

        return ResponseEntity.ok(response);
    }

    // ── Issue #2101 — GET /api/events/{id} ──────────────────────────────────

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a public event by ID",
            description = "Fetches a public event using its unique event ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event fetched successfully",
                    content = @Content(
                            schema = @Schema(implementation = Event.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found or not public",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Event> getPublicEventById(
            @Parameter(description = "ID of the public event")
            @PathVariable Long id) {

        Event event = eventService.getPublicEventById(id);
        return ResponseEntity.ok(event);
    }

    // ── Issue #2101 — GET /api/events/{id}/availability ─────────────────────

    @GetMapping("/{id}/availability")
    @Operation(
            summary = "Get event availability",
            description =
                    "Returns capacity, registered users count, remaining spots, " +
                    "and whether the event has already passed."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event availability fetched successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = EventAvailabilityResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<EventAvailabilityResponse> getEventAvailability(
            @Parameter(description = "ID of the event")
            @PathVariable Long id) {

        EventAvailabilityResponse response =
                eventService.getEventAvailability(id);

        return ResponseEntity.ok(response);
    }

    // ── Issue #2102 — POST /api/events/{id}/register ─────────────────────────

    @PostMapping("/{id}/register")
    @Operation(
            summary = "Register the authenticated user for an event",
            description =
                    "Registers the currently authenticated user for a specific event. " +
                    "Returns 409 if the event is full or the user is already registered.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully registered for event",
                    content = @Content(
                            schema = @Schema(
                                    implementation = RegistrationResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event or user not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Event is already full or user already registered",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<RegistrationResponse> registerForEvent(
            @Parameter(description = "ID of the event to register for")
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        RegistrationResponse response =
                eventService.registerUserForEvent(id, userEmail);

        return ResponseEntity.ok(response);
    }

    private static final java.util.Set<String> ALLOWED_SORT_FIELDS = java.util.Set.of(
            "eventDate", "title", "category", "status", "registeredCount"
    );

    private Pageable buildPageable(int page, int size, String sort) {
        Sort springSort;
        try {
            String[] parts     = sort.split(",");
            String   field     = parts[0].trim();
            String   direction = parts.length > 1 ? parts[1].trim() : "asc";

            if (!ALLOWED_SORT_FIELDS.contains(field)) {
                field = "eventDate";
            }

            springSort = direction.equalsIgnoreCase("desc")
                    ? Sort.by(Sort.Direction.DESC, field)
                    : Sort.by(Sort.Direction.ASC,  field);

        } catch (Exception e) {
            springSort = Sort.by(Sort.Direction.ASC, "eventDate");
        }

        return PageRequest.of(page, size, springSort);
    }
}