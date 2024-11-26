package com.eventscheduler.service;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventConflictService {

    private final EventQueryService eventQueryService;

    @Autowired
    public EventConflictService(EventQueryService eventQueryService) {
        this.eventQueryService = eventQueryService;
    }

    public boolean hasConflict(EventDto newEvent) {
        List<Event> overlappingEvents = eventQueryService.findEventsInRange(
                newEvent.getStartTime(),
                newEvent.getEndTime()
        );
        return overlappingEvents.stream().anyMatch(existingEvent ->
                isOverlap(newEvent, existingEvent)
        );
    }

    private boolean isOverlap(EventDto newEvent, Event existingEvent) {
        return newEvent.getStartTime().isBefore(existingEvent.getEndTime()) &&
                newEvent.getEndTime().isAfter(existingEvent.getStartTime());
    }
}
