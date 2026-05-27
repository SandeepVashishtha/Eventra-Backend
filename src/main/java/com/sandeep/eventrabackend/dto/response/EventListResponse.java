package com.sandeep.eventrabackend.dto.response;

import com.sandeep.eventrabackend.model.Event;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class EventListResponse {
    private List<EventSummary> events;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean first;
    private boolean last;

    public static EventListResponse from(Page<Event> page) {
        EventListResponse response = new EventListResponse();
        response.events        = page.getContent().stream().map(EventSummary::from).toList();
        response.currentPage   = page.getNumber();
        response.totalPages    = page.getTotalPages();
        response.totalElements = page.getTotalElements();
        response.pageSize      = page.getSize();
        response.first         = page.isFirst();
        response.last          = page.isLast();
        return response;
    }

    public static class EventSummary {

        private Long          id;
        private String        title;
        private String        description;
        private String        location;
        private LocalDateTime eventDate;
        private String        category;
        private String        status;
        private Integer       capacity;
        private int           registeredCount;
        private boolean       eventPast;

        public static EventSummary from(Event event) {
            EventSummary s = new EventSummary();
            s.id              = event.getId();
            s.title           = event.getTitle();
            s.description     = event.getDescription();
            s.location        = event.getLocation();
            s.eventDate       = event.getEventDate();
            s.category        = event.getCategory();
            s.status          = event.getStatus() != null ? event.getStatus().name() : null;
            s.capacity        = event.getCapacity();
            s.registeredCount = event.getRegisteredCount();
            s.eventPast       = event.isEventPast();
            return s;
        }

        // Getters
        public Long          getId()              { return id; }
        public String        getTitle()           { return title; }
        public String        getDescription()     { return description; }
        public String        getLocation()        { return location; }
        public LocalDateTime getEventDate()       { return eventDate; }
        public String        getCategory()        { return category; }
        public String        getStatus()          { return status; }
        public Integer       getCapacity()        { return capacity; }
        public int           getRegisteredCount() { return registeredCount; }
        public boolean       isEventPast()        { return eventPast; }
    }

    // Getters
    public List<EventSummary> getEvents()        { return events; }
    public int                getCurrentPage()   { return currentPage; }
    public int                getTotalPages()    { return totalPages; }
    public long               getTotalElements() { return totalElements; }
    public int                getPageSize()      { return pageSize; }
    public boolean            isFirst()          { return first; }
    public boolean            isLast()           { return last; }
}
