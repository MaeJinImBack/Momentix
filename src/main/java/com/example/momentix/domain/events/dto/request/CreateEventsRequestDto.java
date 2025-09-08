package com.example.momentix.domain.events.dto.request;


import com.example.momentix.domain.events.entity.enums.AgeRatingType;
import com.example.momentix.domain.events.entity.enums.EventCategoryType;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventsRequestDto {

    // Event Entity 값
    private String eventTitle;
    private EventCategoryType eventCategory;
    private AgeRatingType ageRating;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;

    // Place Entity값
    private String placeName;
    private String placeAddress;


    // EventTimes Entity값
    private List<EventTimes> eventTimeList;

    // ReservationTimes Entity 값
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;

    // Casts Entity 값
    private String castName;


}


