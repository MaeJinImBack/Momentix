package com.example.momentix.domain.events.repository.eventtimes;

import com.example.momentix.domain.events.dto.request.SearchSeatRequestDto;
import com.example.momentix.domain.events.dto.response.ReserveSeatResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventTimeReserveSeatRepositoryCustom {
    Page<ReserveSeatResponseDto> searchAllSeat(SearchSeatRequestDto requestDto, Pageable pageable);
}
