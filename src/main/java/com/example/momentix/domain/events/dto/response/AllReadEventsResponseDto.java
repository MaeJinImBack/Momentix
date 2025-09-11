package com.example.momentix.domain.events.dto.response;

import com.example.momentix.domain.events.entity.enums.EventCategoryType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class AllReadEventsResponseDto {
    private String eventTitle;
    private String eventCategory;
    private String placeName;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;

    public AllReadEventsResponseDto(String eventTitle, EventCategoryType eventCategory, LocalDate eventStartDate, LocalDate eventEndDate, String placeName) {
        this.eventTitle = eventTitle;
        this.eventCategory = eventCategory.name();
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.placeName = placeName;

    }

}
