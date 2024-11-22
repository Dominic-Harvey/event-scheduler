package com.eventscheduler.service;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.model.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EventService {
    Event createEvent(EventDto event);
    List<Event> getAllEvents();
    Optional<Event> getEventById(Long id);
    EventDto toEventDto(Event event);
    List<EventDto> toEventDtoList(List<Event> events);
}
