package com.example.momentix.domain.events.dto.request;

import com.example.momentix.domain.events.entity.enums.AgeRatingType;
import com.example.momentix.domain.events.entity.enums.EventCategoryType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UpdateBaseEventRequestDto {
    // Event Entity 값
    private String eventTitle;
    private EventCategoryType eventCategory;
    private AgeRatingType ageRating;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
}
