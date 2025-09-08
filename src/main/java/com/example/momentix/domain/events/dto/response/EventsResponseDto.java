package com.example.momentix.domain.events.dto.response;

import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.casts.Casts;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.events.entity.places.Places;
import com.example.momentix.domain.events.entity.reservationtimes.ReservationTimes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventsResponseDto {
    private String eventTitle;
    private String eventCategory;
    private String ageRating;
    private String placeName;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
    private String castName;

    public EventsResponseDto(Events events, Places places, EventTimes eventTimes, ReservationTimes reservationTimes, Casts casts) {
        this.eventTitle = events.getEventTitle();
        this.eventCategory = events.getEventCategoryType().getEventGenre();
        this.ageRating = events.getAgeRatingType().getDescription();

        this.placeName = places.getPlaceName();

        this.eventStartTime = eventTimes.getEventStartTime();
        this.eventEndTime = eventTimes.getEventEndTime();

        this.reservationStartTime = reservationTimes.getReservationStartTime();
        this.reservationEndTime = reservationTimes.getReservationEndTime();

        this.castName = casts.getCastName();
    }

}
