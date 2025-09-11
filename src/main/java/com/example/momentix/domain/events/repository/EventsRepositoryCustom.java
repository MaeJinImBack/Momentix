package com.example.momentix.domain.events.repository;

import com.example.momentix.domain.events.dto.response.ReadEventResponseDto;

public interface EventsRepositoryCustom {
    public ReadEventResponseDto searchEventById(Long eventId, Long placeId);
}
