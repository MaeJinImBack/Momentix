package com.example.momentix.domain.events.dto.response;

import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.casts.Casts;
import com.example.momentix.domain.events.entity.places.Places;
import com.example.momentix.domain.events.entity.reservationtimes.ReservationTimes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventsResponseDto {
    private String eventTitle;
    private String eventCategory;
    private String ageRating;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;

    private String placeName;

    private List<EventTimeResponseDto> eventTimesList;

    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
    private String castName;

    public EventsResponseDto(Events events, Places places, ReservationTimes reservationTimes, Casts casts) {
        this.eventTitle = events.getEventTitle();
        this.eventCategory = events.getEventCategoryType().getEventGenre();
        this.ageRating = events.getAgeRatingType().getDescription();
        this.eventStartDate = events.getEventStartDate();
        this.eventEndDate = events.getEventEndDate();

        this.placeName = places.getPlaceName();
        this.eventTimesList = events.getEventTimeList().stream()
                .map(eventTimes -> new EventTimeResponseDto(
                        eventTimes.getEventStartTime(),
                        eventTimes.getEventEndTime()
                ))
                .collect(Collectors.toList());
        this.reservationStartTime = reservationTimes.getReservationStartTime();
        this.reservationEndTime = reservationTimes.getReservationEndTime();

        this.castName = casts.getCastName();
    }

}
