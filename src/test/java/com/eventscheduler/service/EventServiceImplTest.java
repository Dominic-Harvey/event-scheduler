package com.eventscheduler.service;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.exception.BadRequestException;
import com.eventscheduler.exception.ConflictException;
import com.eventscheduler.model.Event;
import com.eventscheduler.repository.EventRepository;
import com.eventscheduler.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEvent_shouldThrowConflictExceptionWhenOverlapping() {
        Event existingEvent = Event.builder()
                .id(1L)
                .name("Existing Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 9, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 11, 0))
                .build();

        EventDto newEvent = EventDto.builder()
                .name("New Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 10, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 12, 0))
                .build();

        when(eventRepository.findEvents(any(), any())).thenReturn(List.of(existingEvent));

        ConflictException exception = assertThrows(ConflictException.class, () -> eventService.createEvent(newEvent));
        assertEquals("The event conflicts with an existing event.", exception.getMessage());

        verify(eventRepository, times(1)).findEvents(any(), any());
    }

    @Test
    void createEvent_shouldThrowConflictExceptionWhenEventInsideAnother() {
        EventDto newEvent = EventDto.builder()
                .name("New Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 10, 30))
                .endTime(LocalDateTime.of(2024, 11, 22, 11, 30))
                .build();

        Event existingEvent = Event.builder()
                .id(1L)
                .name("Existing Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 10, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 12, 0))
                .build();

        when(eventRepository.findEvents(any(), any())).thenReturn(List.of(existingEvent));

        ConflictException exception = assertThrows(ConflictException.class, () -> eventService.createEvent(newEvent));
        assertEquals("The event conflicts with an existing event.", exception.getMessage());
        verify(eventRepository, times(1)).findEvents(any(), any());
    }

    @Test
    void createEvent_shouldSaveEventWhenEventsTouchEndToStart() {
        EventDto newEvent = EventDto.builder()
                .name("New Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 12, 0)) // Starts when existingEvent ends
                .endTime(LocalDateTime.of(2024, 11, 22, 13, 0))
                .build();

        Event existingEvent1 = Event.builder()
                .id(1L)
                .name("Morning Meeting")
                .startTime(LocalDateTime.of(2024, 11, 22, 11, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 12, 0)) // Ends exactly when newEvent starts
                .build();

        Event existingEvent2 = Event.builder()
                .id(2L)
                .name("Afternoon Meeting")
                .startTime(LocalDateTime.of(2024, 11, 22, 13, 0)) // Starts exactly when newEvent ends
                .endTime(LocalDateTime.of(2024, 11, 22, 14, 0))
                .build();

        Event savedEvent = Event.builder()
                .id(3L)
                .name(newEvent.getName())
                .startTime(newEvent.getStartTime())
                .endTime(newEvent.getEndTime())
                .build();

        when(eventRepository.findEvents(any(), any())).thenReturn(List.of(existingEvent1, existingEvent2));
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        Event result = eventService.createEvent(newEvent);

        assertNotNull(result);
        assertEquals(newEvent.getName(), result.getName());
        assertEquals(newEvent.getStartTime(), result.getStartTime());
        assertEquals(newEvent.getEndTime(), result.getEndTime());
        assertNotNull(result.getId());
        assertEquals(3L, result.getId());

        verify(eventRepository, times(1)).findEvents(any(), any());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void createEvent_shouldSaveEventWhenNoConflict() {
        EventDto newEvent = EventDto.builder()
                .name("New Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 13, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 14, 0))
                .build();

        Event savedEvent = Event.builder()
                .id(1L)
                .name(newEvent.getName())
                .startTime(newEvent.getStartTime())
                .endTime(newEvent.getEndTime())
                .build();

        when(eventRepository.findEvents(any(), any())).thenReturn(Collections.emptyList());
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        Event result = eventService.createEvent(newEvent);

        assertNotNull(result);
        assertEquals(newEvent.getName(), result.getName());
        assertEquals(newEvent.getStartTime(), result.getStartTime());
        assertEquals(newEvent.getEndTime(), result.getEndTime());
        assertNotNull(result.getId());

        verify(eventRepository, times(1)).findEvents(any(), any());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void getEvents_shouldReturnAllEvents() {
        Event event = Event.builder()
                .id(1L)
                .name("Test Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 10, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 12, 0))
                .build();

        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<Event> events = eventService.getAllEvents();

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Test Event", events.get(0).getName());

        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void getEventById_shouldReturnEventIfExists() {
        Long eventId = 1L;
        Event event = Event.builder()
                .id(eventId)
                .name("Test Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 10, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 12, 0))
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Optional<Event> result = eventService.getEventById(eventId);

        assertTrue(result.isPresent());
        assertEquals(event.getName(), result.get().getName());

        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void validateStartAndEndTime_shouldThrowBadRequestExceptionForInvalidTimeRange() {
        LocalDateTime startTime = LocalDateTime.of(2024, 11, 22, 12, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 11, 22, 10, 0);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                eventService.getEvents(startTime, endTime));

        assertEquals("Start time must be before end time", exception.getMessage());
    }
}