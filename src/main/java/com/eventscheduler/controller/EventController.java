package com.eventscheduler.controller;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.exception.EventNotFoundException;
import com.eventscheduler.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get all events
     *
     * @return List of all events
     */
    @GetMapping
    public ResponseEntity<List<EventDto>> getEvents(
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {

        return ResponseEntity.ok(
                eventService.toEventDtoList(eventService.getEvents(startTime, endTime))
        );
    }

    /**
     * Get a specific event by ID
     *
     * @param id of the event to fetch
     * @return one event by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(eventService::toEventDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + id));
    }

    /**
     * Create a new event
     *
     * @param event to create
     * @return created event
     */
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody @Validated EventDto event) {
        return ResponseEntity.ok(eventService.toEventDto(eventService.createEvent(event)));
    }
}