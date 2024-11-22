package com.eventscheduler.service.impl;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.exception.BadRequestException;
import com.eventscheduler.exception.ConflictException;
import com.eventscheduler.model.Event;
import com.eventscheduler.repository.EventRepository;
import com.eventscheduler.service.EventService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
    public List<Event> getEvents(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null || endTime != null) {
            return findEventsInRange(startTime, endTime);
        }
        return getAllEvents();
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

    private List<Event> findEventsInRange(LocalDateTime startTime, LocalDateTime endTime) {
        validateStartAndEndTime(startTime, endTime);
        return eventRepository.findEvents(startTime, endTime);
    }

    private void validateEventDto(EventDto eventDto) {
        if (hasConflict(eventDto)) {
            throw new ConflictException("The event conflicts with an existing event.");
        }
    }

    private void validateStartAndEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BadRequestException("Both start time and end time must be provided");
        }

        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("Start time must be before end time");
        }
    }

    private boolean hasConflict(EventDto newEvent) {
        // Fetch all potentially overlapping events
        List<Event> inclusiveEvents = findEventsInRange(
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
