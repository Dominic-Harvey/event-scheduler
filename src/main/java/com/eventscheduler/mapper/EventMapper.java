package com.eventscheduler.mapper;

import com.eventscheduler.dto.EventDto;
import com.eventscheduler.model.Event;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public Event toEntity(EventDto eventDto) {
        return Event.builder()
                .name(eventDto.getName())
                .startTime(eventDto.getStartTime())
                .endTime(eventDto.getEndTime())
                .build();
    }

    public EventDto toEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .build();
    }

    public List<EventDto> toEventDtoList(List<Event> events) {
        return events.stream()
                .map(this::toEventDto)
                .collect(Collectors.toList());
    }
}