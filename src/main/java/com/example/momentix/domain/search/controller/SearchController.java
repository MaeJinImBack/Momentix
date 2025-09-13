package com.example.momentix.domain.search.controller;

import com.example.momentix.domain.search.dto.SearchRequestDto;
import com.example.momentix.domain.search.dto.SearchResponseDto;
import com.example.momentix.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping

    public ResponseEntity<Page<SearchResponseDto>> searchEvent(@ModelAttribute SearchRequestDto searchRequestdto, Pageable pageable) {
        Page<SearchResponseDto> response = searchService.searchEvent(searchRequestdto, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
