package com.eventra.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventra.dto.EventDTO;
import com.eventra.entity.Event;
import com.eventra.repository.EventsRepository;

@Service
public class EventService {
@Autowired
private EventsRepository rep;

    //get all events
    public List<Event> getAllEvents() {
        return rep.findAll();
    }

    //create an event
    public Event createEvent(EventDTO eventDto){
        Event event=new Event();
         event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setLocation(eventDto.getLocation());
        event.setOrganizer(eventDto.getOrganizer());
        
        event.setCategory(eventDto.getCategory());
        event.setDate(eventDto.getDate());
event.setStatus(eventDto.getStatus());

        // Default handling
        if (eventDto.getCapacity() == 0) {
            event.setCapacity(100); // default
        } else {
            event.setCapacity(eventDto.getCapacity());
        }

        return rep.save(event);

    }
    
}
