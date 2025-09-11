package com.example.momentix.domain.events.repository.eventtimes;

import com.example.momentix.domain.events.dto.request.SearchSeatRequestDto;
import com.example.momentix.domain.events.dto.response.PartRowColSeatResponseDto;
import com.example.momentix.domain.events.dto.response.ReserveSeatResponseDto;
import com.example.momentix.domain.events.entity.enums.SeatPartType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventTimeReserveSeatRepositoryCustom {
    Page<ReserveSeatResponseDto> searchAllSeat(SearchSeatRequestDto requestDto, Pageable pageable);
    Page<PartRowColSeatResponseDto> searchPartSeat(
            SearchSeatRequestDto requestDto,
            SeatPartType partType,
            Long rowId,
            Long colId,
            Pageable pageable);
}
