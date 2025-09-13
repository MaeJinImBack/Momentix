package com.example.momentix.domain.reservation.dto;

import com.example.momentix.domain.reservation.entity.ReservationStatusType;
import com.example.momentix.domain.reservation.entity.Reservations;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
public class ReservationResponseDto {
    private Long reservationId;

    private Long usersId;

    private Long eventsId;

    private Long eventPlaceId;

    private Long eventTimeId;

    private Long eventSeatId;

    private ReservationStatusType reservationStatusType;

    private String eventTitle;
    private String eventCategoryType;
    private String ageRatingType;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;

    public static ReservationResponseDto from(Reservations r) {
        var e = r.getEvents();
        return  ReservationResponseDto.builder()
                .reservationId(r.getReservationId())
                .usersId(r.getUsers().getUserId())
                .eventsId(r.getEvents().getId())
                .eventPlaceId(r.getEventPlace() == null ? null : r.getEventPlace().getId())
                .eventTimeId(r.getEventTimes() == null ? null : r.getEventTimes().getId())
                .eventSeatId(r.getEventSeat() == null ? null : r.getEventSeat().getId())
                .reservationStatusType(r.getReservationStatusType())

                .eventTitle(e.getEventTitle())
                .eventCategoryType(e.getEventCategoryType() == null ? null : e.getEventCategoryType().name())
                .ageRatingType(e.getAgeRatingType() == null ? null : e.getAgeRatingType().name())
                .eventStartDate(e.getEventStartDate())
                .eventEndDate(e.getEventEndDate())
                .build();
    }

    public static ReservationResponseDto fromWithEvent(Reservations r) {
        var e = r.getEvents();
        return ReservationResponseDto.builder()
                .reservationId(r.getReservationId())
                .usersId(r.getUsers().getUserId())
                .eventsId(e.getId())
                .eventPlaceId(r.getEventPlace() == null ? null : r.getEventPlace().getId())
                .eventTimeId(r.getEventTimes() == null ? null : r.getEventTimes().getId())
                .eventSeatId(r.getEventSeat() == null ? null : r.getEventSeat().getId())
                .reservationStatusType(r.getReservationStatusType())
                .eventTitle(e.getEventTitle())
                .eventCategoryType(e.getEventCategoryType() == null ? null : e.getEventCategoryType().name())
                .ageRatingType(e.getAgeRatingType() == null ? null : e.getAgeRatingType().name())
                .build();
    }

}
