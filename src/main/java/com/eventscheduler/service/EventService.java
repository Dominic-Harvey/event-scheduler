package com.eventscheduler.service;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.exception.ConflictException;
import com.eventscheduler.mapper.EventMapper;
import com.eventscheduler.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventValidator eventValidator;
    private final EventConflictService eventConflictService;
    private final EventMapper eventMapper;
    private final EventQueryService eventQueryService;
    private final EventPersistenceService eventPersistenceService;

    @Autowired
    public EventService(EventValidator eventValidator,
                        EventConflictService eventConflictService,
                        EventMapper eventMapper,
                        EventQueryService eventQueryService,
                        EventPersistenceService eventPersistenceService) {
        this.eventValidator = eventValidator;
        this.eventConflictService = eventConflictService;
        this.eventMapper = eventMapper;
        this.eventQueryService = eventQueryService;
        this.eventPersistenceService = eventPersistenceService;
    }

    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        eventValidator.validateStartAndEndTime(eventDto.getStartTime(), eventDto.getEndTime());
        if (eventConflictService.hasConflict(eventDto)) {
            throw new ConflictException("The event conflicts with an existing event.");
        }

        Event event = eventMapper.toEntity(eventDto);
        Event savedEvent = eventPersistenceService.saveEvent(event);
        return eventMapper.toEventDto(savedEvent);
    }

    public List<EventDto> getAllEvents() {
        List<Event> events = eventQueryService.getAllEvents();
        return eventMapper.toEventDtoList(events);
    }

    public List<EventDto> getEvents(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null) {
            eventValidator.validateStartAndEndTime(startTime, endTime);
            List<Event> events = eventQueryService.findEventsInRange(startTime, endTime);
            return eventMapper.toEventDtoList(events);
        } else {
            return getAllEvents();
        }
    }

    public Optional<EventDto> getEventById(Long id) {
        Optional<Event> eventOpt = eventQueryService.getEventById(id);
        return eventOpt.map(eventMapper::toEventDto);
    }
}