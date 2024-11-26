package com.eventscheduler.controller;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    void createEvent_shouldReturnCreatedEventDto() throws Exception {
        EventDto requestEvent = EventDto.builder()
                .name("New Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 12, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 13, 0))
                .build();

        EventDto responseEvent = EventDto.builder()
                .id(1L)
                .name("New Event")
                .startTime(LocalDateTime.of(2024, 11, 22, 12, 0))
                .endTime(LocalDateTime.of(2024, 11, 22, 13, 0))
                .build();

        // Mock the EventService to return the response DTO when createEvent is called
        when(eventService.createEvent(any(EventDto.class))).thenReturn(responseEvent);

        // Perform the POST request and verify the response
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestEvent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Event"))
                .andExpect(jsonPath("$.startTime").value("2024-11-22T12:00:00"))
                .andExpect(jsonPath("$.endTime").value("2024-11-22T13:00:00"));

        // Verify that the EventService.createEvent was called once with any EventDto
        verify(eventService, times(1)).createEvent(any(EventDto.class));
    }
}