package com.example.momentix.domain.reservation.entity;


import com.example.momentix.domain.events.entity.EventPlace;
import com.example.momentix.domain.events.entity.EventSeat;
import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Entity
@Table(
        name = "reservations",
        //공연 시간과 좌석을 unique 처리 -> 해당 공연 시간의 좌석은 1개
        uniqueConstraints = {
            @UniqueConstraint(name = "unique_reservation_event_time_event_seat",
            columnNames = {"event_time_id","event_seat_id"}) }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Events events;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_place_id")
    private EventPlace eventPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_time_id")
    private EventTimes eventTimes;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_seat_id")
    private EventSeat eventSeat;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status_type", nullable = false,length = 20)
    private ReservationStatusType reservationStatusType;

    @Builder
    public Reservations(Users users, Events events, ReservationStatusType reservationStatusType){
        this.users = users;
        this.events= events;
        this.reservationStatusType =reservationStatusType;
    }

    //장소 선택 시
    public void selectEventPlace(EventPlace eventPlace){
        this.eventPlace = eventPlace;
        this.eventTimes = null;
        this.eventSeat = null;
        this.reservationStatusType = ReservationStatusType.SELECT_PLACE;
    }

    //시간 선택 시
    public void selectEventTime(EventTimes eventTimes) {
        this.eventTimes =eventTimes;
        this.eventSeat = null;
        this.reservationStatusType = ReservationStatusType.SELECT_TIME;
    }

    //좌석 선택
    public void selectEventSeat(EventSeat seat) {
        this.eventSeat = seat;
        this.reservationStatusType = ReservationStatusType.SELECT_SEAT;
    }

}
