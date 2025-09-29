package com.example.momentix.domain.events.repository;

import com.example.momentix.domain.events.dto.response.ReadEventResponseDto;
import com.example.momentix.domain.search.dto.SearchRequestDto;
import com.example.momentix.domain.search.dto.SearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventsRepositoryCustom {
    ReadEventResponseDto searchEventById(Long eventId, Long placeId);

    Page<SearchResponseDto> searchEventByParam(SearchRequestDto searchRequestDto, Pageable pageable);
}
