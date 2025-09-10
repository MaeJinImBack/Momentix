package com.example.momentix.domain.reservation.entitiy;


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
    @JoinColumn(name = "event_time_id")
    private EventTimes eventTimes;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_seat_id")
    private EventSeat eventSeat;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status_type", nullable = false)
    private ReservationStatusType reservationStatusType;

    @Builder
    public Reservations(Users users, Events events, ReservationStatusType reservationStatusType){
        this.users = users;
        this.events= events;
        this.reservationStatusType =reservationStatusType;
    }


}
