package com.eventscheduler.service.impl;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.exception.BadRequestException;
import com.eventscheduler.exception.ConflictException;
import com.eventscheduler.model.Event;
import com.eventscheduler.repository.EventRepository;
import com.eventscheduler.service.EventService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public Event createEvent(EventDto event) {
        validateEventDto(event);

        return eventRepository.save(Event.builder()
                .name(event.getName())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .build()
        );
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public EventDto toEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .build();
    }

    @Override
    public List<EventDto> toEventDtoList(List<Event> events) {
        return events.stream()
                .map(this::toEventDto)
                .toList();
    }
    private void validateEventDto(EventDto eventDto) {
        if (!eventDto.getStartTime().isBefore(eventDto.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }

        if (hasConflict(eventDto)) {
            throw new ConflictException("The event conflicts with an existing event.");
        }
    }

    private boolean hasConflict(EventDto newEvent) {
        // Fetch all potentially overlapping events
        List<Event> inclusiveEvents = eventRepository.findInclusiveEvents(
                newEvent.getStartTime(),
                newEvent.getEndTime()
        );

        // Check each event for actual overlaps
        return inclusiveEvents.stream().anyMatch(existingEvent ->
                isOverlap(newEvent, existingEvent)
        );
    }

    private boolean isOverlap(EventDto newEvent, Event existingEvent) {
        // Check if the new event overlaps with the existing event
        return (newEvent.getStartTime().isBefore(existingEvent.getEndTime()) &&
                newEvent.getEndTime().isAfter(existingEvent.getStartTime()));
    }
}
