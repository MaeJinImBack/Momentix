package com.example.momentix.domain.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EventTimeResponseDto {
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;

    public EventTimeResponseDto(LocalDateTime eventStartTime, LocalDateTime eventEndTime) {
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }
}
