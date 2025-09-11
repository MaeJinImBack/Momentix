package com.example.momentix.domain.events.dto.request;

import com.example.momentix.domain.events.entity.enums.AgeRatingType;
import com.example.momentix.domain.events.entity.enums.EventCategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBaseEventRequestDto {
    // Event Entity 값
    private String eventTitle;
    private EventCategoryType eventCategory;
    private AgeRatingType ageRating;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
}
