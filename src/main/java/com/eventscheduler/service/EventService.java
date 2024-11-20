package com.eventscheduler.service;

import com.eventscheduler.model.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EventService {
    Event createEvent(Event event);
    List<Event> getAllEvents();
    Optional<Event> getEventById(Long id);
    boolean hasConflict(Event event);
}
