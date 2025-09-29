package com.example.momentix.domain.search.dto;

import com.example.momentix.domain.events.entity.enums.EventCategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SearchRequestDto {
    private String eventTitle;
    private EventCategoryType eventCategory;
    private LocalDate searchStartDate;
    private LocalDate searchEndDate;
    private String region;

    private String query; // 사용자가 입력한 검색어

}
