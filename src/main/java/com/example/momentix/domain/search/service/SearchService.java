package com.example.momentix.domain.search.service;

import com.example.momentix.domain.common.elasticsearch.IndexNames;
import com.example.momentix.domain.events.repository.EventsRepository;
import com.example.momentix.domain.search.dto.*;
import com.example.momentix.domain.search.repository.AnalyticsRepository;
import com.example.momentix.domain.search.repository.SuggestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class SearchService {
    private final EventsRepository eventsRepository;


    private final SuggestRepository suggestRepository;
    private final AnalyticsRepository analyticsRepository;

    public SearchService(
            EventsRepository eventsRepository,
            SuggestRepository suggestRepository,
            AnalyticsRepository analyticsRepository
    ) {
        this.eventsRepository = eventsRepository;
        this.suggestRepository = suggestRepository;
        this.analyticsRepository = analyticsRepository;
    }

    @Transactional(readOnly = true)
    public Page<SearchResponseDto> searchEvent(SearchRequestDto searchRequestdto, Pageable pageable) {
        return eventsRepository.searchEventByParam(searchRequestdto, pageable);
    }

    //엘라스틱서치
    @Transactional(readOnly = true)
    public List<AutocompleteResponse> autocomplete(String input, int size) {
        int limit = size > 0 ? size : IndexNames.DEFAULT_SUGGEST_SIZE;
        return suggestRepository.suggest(input, limit);
    }

    @Transactional(readOnly = true)
    public List<HourlyCountBucket> hourlyCounts(int hours) {
        return analyticsRepository.countPerHour(hours);
    }

    @Transactional(readOnly = true)
    public List<AutocompleteResponse> popularQueries(int hours, int size) {
        return analyticsRepository.popularQueries(hours, size);
    }


    // 인기 검색어 로그 집계
    @Async
    public void logSearchAsync(SearchRequestDto req, String userId, String ip) {
        if (req == null || req.getQuery() == null || req.getQuery().isBlank())
            return;

        String category = (req.getEventCategory() == null) ? null : req.getEventCategory().name();
        String start = (req.getSearchStartDate() == null) ? null : req.getSearchStartDate().toString();
        String end = (req.getSearchEndDate() == null) ? null : req.getSearchEndDate().toString();

        SearchLogDoc doc = new SearchLogDoc(
                req.getQuery(),
                category,
                start,
                end,
                userId,
                ip,
                Instant.now().toEpochMilli()
        );

        analyticsRepository.indexSearchLog(doc);
    }
}
