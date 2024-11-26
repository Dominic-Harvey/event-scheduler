package com.eventscheduler.service;

import com.eventscheduler.model.Event;
import com.eventscheduler.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventPersistenceService {

    private final EventRepository eventRepository;

    @Autowired
    public EventPersistenceService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
}