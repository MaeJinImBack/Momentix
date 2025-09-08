package com.example.momentix.domain.events.dto.request;


import com.example.momentix.domain.events.entity.enums.AgeRatingType;
import com.example.momentix.domain.events.entity.enums.EventCategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventsRequestDto {

    // Event Entity 값
    private String eventTitle;
    private EventCategoryType eventCategory;
    private AgeRatingType ageRating;

    // Place Entity값
    private String placeName;
    private String placeAddress;

    // EventTimes Entity값
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;

    // ReservationTimes Entity 값
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;

    // Casts Entity 값
    private String castName;

}
