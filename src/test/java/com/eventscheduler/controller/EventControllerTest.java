package com.eventscheduler.controller;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.model.Event;
import com.eventscheduler.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void createEvent_shouldReturnCreatedEventDto() throws Exception {
        EventDto requestEvent = EventDto.builder()
                .name("New Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 12, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 13, 0))
                .build();

        Event savedEvent = Event.builder()
                .id(1L)
                .name(requestEvent.getName())
                .startTime(requestEvent.getStartTime())
                .endTime(requestEvent.getEndTime())
                .build();

        EventDto responseEvent = EventDto.builder()
                .id(1L)
                .name("New Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 12, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 13, 0))
                .build();

        when(eventService.createEvent(any(EventDto.class))).thenReturn(savedEvent);
        when(eventService.toEventDto(any(Event.class))).thenReturn(responseEvent);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestEvent))) // Use configured ObjectMapper here
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Event"))
                .andExpect(jsonPath("$.startTime").value("2024-11-22T12:00:00"))
                .andExpect(jsonPath("$.endTime").value("2024-11-22T13:00:00"));

        // Verify interactions with the mocked service
        verify(eventService, times(1)).createEvent(any(EventDto.class));
        verify(eventService, times(1)).toEventDto(any(Event.class));
    }
}
