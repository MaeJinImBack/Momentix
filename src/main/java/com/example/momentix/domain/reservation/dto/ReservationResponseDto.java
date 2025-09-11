package com.example.momentix.domain.reservation.dto;

import com.example.momentix.domain.reservation.entity.ReservationStatusType;
import com.example.momentix.domain.reservation.entity.Reservations;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
public class ReservationResponseDto {

    private Long usersId;

    private Long eventsId;

    private Long eventPlaceId;

    private Long eventTimeId;

    private Long eventSeatId;

    private ReservationStatusType reservationStatusType;


    public static ReservationResponseDto from(Reservations r) {

        return  ReservationResponseDto.builder()
                .usersId(r.getUsers().getUserId())
                .eventsId(r.getEvents().getId())
                .eventPlaceId(r.getEventPlace().getId())
                .eventTimeId(r.getEventTimes() == null ? null : r.getEventTimes().getId())
                .eventSeatId(r.getEventSeat() == null ? null : r.getEventSeat().getId())
                .reservationStatusType(r.getReservationStatusType())
                .build();
    }
}
