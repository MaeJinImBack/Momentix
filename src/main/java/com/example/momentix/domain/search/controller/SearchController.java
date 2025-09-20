package com.example.momentix.domain.search.controller;

import com.example.momentix.domain.search.dto.AutocompleteResponse;
import com.example.momentix.domain.search.dto.HourlyCountBucket;
import com.example.momentix.domain.search.dto.SearchRequestDto;
import com.example.momentix.domain.search.dto.SearchResponseDto;
import com.example.momentix.domain.search.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<Page<SearchResponseDto>> searchEvent(
            @ModelAttribute SearchRequestDto searchRequestDto,
            Pageable pageable,
            HttpServletRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        Page<SearchResponseDto> response = searchService.searchEvent(searchRequestDto, pageable);
        // 카테고리/startDate/endDate같은 필터도 있으면 같이 기록되게
        searchService.logSearchAsync(
                searchRequestDto,
                (userId == null) ? null : String.valueOf(userId),
                extractClientIp(request)
        );

        return ResponseEntity.ok(response);
    }

    private String extractClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank())
            return xff.split(",")[0].trim();
        String realIp = req.getHeader("X-Real-IP");
        return (realIp != null && !realIp.isBlank()) ? realIp : req.getRemoteAddr();
    }

    // 자동완성
    @GetMapping("/autocomplete")
    public List<AutocompleteResponse> autocomplete(
            @RequestParam("q") String query,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return searchService.autocomplete(query, size);
    }

    // 인기검색어
    @GetMapping("/popular-queries")
    public List<AutocompleteResponse> popularQueries(
            @RequestParam(value = "hours", defaultValue = "24") int hours,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return searchService.popularQueries(hours, size);
    }

    // 시간대별 건수
    @GetMapping("/hourly-counts")
    public List<HourlyCountBucket> hourlyCounts(
            @RequestParam(value = "hours", defaultValue = "24") int hours) {
        return searchService.hourlyCounts(hours);
    }
}
