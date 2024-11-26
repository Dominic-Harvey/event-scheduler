package com.eventscheduler.service;

import com.eventscheduler.model.Event;
import com.eventscheduler.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventQueryService {

    private final EventRepository eventRepository;

    @Autowired
    public EventQueryService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> findEventsInRange(LocalDateTime startTime, LocalDateTime endTime) {
        return eventRepository.findEvents(startTime, endTime);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }
}