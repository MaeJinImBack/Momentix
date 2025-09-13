package com.example.momentix.domain.events.repository;

import com.example.momentix.domain.events.dto.response.SeatResponseDto;

import java.util.List;

public interface EventSeatRepositoryCustom {
    void updateEventSeatListByEventsIdAndPlaceId(Long eventsId, Long placeId, List<SeatResponseDto> seatList);
}
