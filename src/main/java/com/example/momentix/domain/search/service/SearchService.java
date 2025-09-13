package com.example.momentix.domain.search.service;

import com.example.momentix.domain.events.repository.EventsRepository;
import com.example.momentix.domain.search.dto.SearchRequestDto;
import com.example.momentix.domain.search.dto.SearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final EventsRepository eventsRepository;

    @Transactional(readOnly = true)
    public Page<SearchResponseDto> searchEvent(SearchRequestDto searchRequestdto, Pageable pageable) {
        return eventsRepository.searchEventByParam(searchRequestdto, pageable);
    }
}
