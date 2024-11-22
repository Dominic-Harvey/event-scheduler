package com.eventscheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class EventDto {
    private final Long id;
    @NotBlank(message = "Event name must not be blank")
    @Size(min = 1, max = 255, message = "Event name must be between 1 and 255 characters")
    private final String name;
    @NotNull(message = "Start time must not be null")
    private final LocalDateTime startTime;
    @NotNull(message = "End time must not be null")
    private final LocalDateTime endTime;
}

