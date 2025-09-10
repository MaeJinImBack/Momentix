package com.example.momentix.domain.events.entity.eventtimes;

import com.example.momentix.domain.events.entity.EventSeat;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class EventTimeReservationSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_time_id")
    private EventTimes eventTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_seat_id")
    private EventSeat eventSeat;
    // 좌석 예약 상태 true일때 예약 가능
    private boolean seatReserveStatus;

}
