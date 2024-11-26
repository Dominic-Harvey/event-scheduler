package com.eventscheduler.service;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.exception.ConflictException;
import com.eventscheduler.mapper.EventMapper;
import com.eventscheduler.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventValidator eventValidator;

    @Mock
    private EventConflictService eventConflictService;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventQueryService eventQueryService;

    @Mock
    private EventPersistenceService eventPersistenceService;

    @InjectMocks
    private EventService eventService;

    private EventDto eventDto;
    private Event event;

    @BeforeEach
    void setUp() {
        eventDto = EventDto.builder()
                .id(1L)
                .name("Test Event")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .build();

        event = Event.builder()
                .id(1L)
                .name("Test Event")
                .startTime(eventDto.getStartTime())
                .endTime(eventDto.getEndTime())
                .build();
    }

    // Test for successful event creation
    @Test
    void createEvent_Success() {
        // Arrange
        when(eventConflictService.hasConflict(eventDto)).thenReturn(false);
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(eventPersistenceService.saveEvent(event)).thenReturn(event);
        when(eventMapper.toEventDto(event)).thenReturn(eventDto);

        // Act
        EventDto createdEvent = eventService.createEvent(eventDto);

        // Assert
        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getId()).isEqualTo(eventDto.getId());
        assertThat(createdEvent.getName()).isEqualTo(eventDto.getName());
        verify(eventValidator).validateStartAndEndTime(eventDto.getStartTime(), eventDto.getEndTime());
        verify(eventConflictService).hasConflict(eventDto);
        verify(eventMapper).toEntity(eventDto);
        verify(eventPersistenceService).saveEvent(event);
        verify(eventMapper).toEventDto(event);
    }

    // Test for event creation conflict
    @Test
    void createEvent_ConflictException() {
        // Arrange
        when(eventConflictService.hasConflict(eventDto)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> eventService.createEvent(eventDto))
                .isInstanceOf(ConflictException.class)
                .hasMessage("The event conflicts with an existing event.");

        verify(eventValidator).validateStartAndEndTime(eventDto.getStartTime(), eventDto.getEndTime());
        verify(eventConflictService).hasConflict(eventDto);
        verify(eventMapper, never()).toEntity(any());
        verify(eventPersistenceService, never()).saveEvent(any());
        verify(eventMapper, never()).toEventDto(any());
    }

    // Test for fetching all events
    @Test
    void getAllEvents() {
        // Arrange
        List<Event> events = Collections.singletonList(event);
        List<EventDto> eventDtos = Collections.singletonList(eventDto);

        when(eventQueryService.getAllEvents()).thenReturn(events);
        when(eventMapper.toEventDtoList(events)).thenReturn(eventDtos);

        // Act
        List<EventDto> result = eventService.getAllEvents();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(eventDto);
        verify(eventQueryService).getAllEvents();
        verify(eventMapper).toEventDtoList(events);
    }

    // Test for fetching events within a time range
    @Test
    void getEvents_WithTimeRange() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        List<Event> events = Collections.singletonList(event);
        List<EventDto> eventDtos = Collections.singletonList(eventDto);

        doNothing().when(eventValidator).validateStartAndEndTime(start, end);

        when(eventQueryService.findEventsInRange(start, end)).thenReturn(events);
        when(eventMapper.toEventDtoList(events)).thenReturn(eventDtos);

        // Act
        List<EventDto> result = eventService.getEvents(start, end);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(eventDto);
        verify(eventValidator).validateStartAndEndTime(start, end);
        verify(eventQueryService).findEventsInRange(start, end);
        verify(eventMapper).toEventDtoList(events);
    }

    // Test for fetching events without time range
    @Test
    void getEvents_WithoutTimeRange() {
        // Arrange
        List<EventDto> eventDtos = Collections.singletonList(eventDto);
        when(eventQueryService.getAllEvents()).thenReturn(Collections.singletonList(event));
        when(eventMapper.toEventDtoList(Collections.singletonList(event))).thenReturn(eventDtos);

        // Act
        List<EventDto> result = eventService.getEvents(null, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(eventDto);
        verify(eventQueryService).getAllEvents();
        verify(eventMapper).toEventDtoList(Collections.singletonList(event));
        verify(eventValidator, never()).validateStartAndEndTime(any(), any());
        verify(eventConflictService, never()).hasConflict(any());
    }

    // Test for fetching an event by ID when it exists
    @Test
    void getEventById_Found() {
        // Arrange
        when(eventQueryService.getEventById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toEventDto(event)).thenReturn(eventDto);

        // Act
        Optional<EventDto> result = eventService.getEventById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(eventDto);
        verify(eventQueryService).getEventById(1L);
        verify(eventMapper).toEventDto(event);
    }

    // Test for fetching an event by ID when it does not exist
    @Test
    void getEventById_NotFound() {
        // Arrange
        when(eventQueryService.getEventById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<EventDto> result = eventService.getEventById(1L);

        // Assert
        assertThat(result).isNotPresent();
        verify(eventQueryService).getEventById(1L);
        verify(eventMapper, never()).toEventDto(any());
    }
}