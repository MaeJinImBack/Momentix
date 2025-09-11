package com.example.momentix.domain.events.repository.seats;

import com.example.momentix.domain.events.dto.response.BaseSeatResponseDto;

import java.util.List;

public interface SeatsRepositoryCustom {
    public void softDeleteSeatByList(List<BaseSeatResponseDto> softDeleteList);
}
