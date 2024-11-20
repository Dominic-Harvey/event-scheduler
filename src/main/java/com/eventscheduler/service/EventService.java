package com.eventscheduler.service;

import com.eventscheduler.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(Event event);
    List<Event> getAllEvents();
    Optional<Event> getEventById(Long id);
    boolean hasConflict(Event event);
}
