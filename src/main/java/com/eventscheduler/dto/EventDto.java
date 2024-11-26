package com.eventscheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private Long id;
    @NotBlank(message = "Event name must not be blank")
    @Size(min = 1, max = 255, message = "Event name must be between 1 and 255 characters")
    private String name;
    @NotNull(message = "Start time must not be null")
    private LocalDateTime startTime;
    @NotNull(message = "End time must not be null")
    private LocalDateTime endTime;
}

