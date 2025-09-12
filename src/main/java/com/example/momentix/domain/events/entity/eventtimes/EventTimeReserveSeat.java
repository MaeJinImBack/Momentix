package com.example.momentix.domain.events.entity.eventtimes;

import com.example.momentix.domain.events.entity.EventSeat;
import com.example.momentix.domain.events.entity.enums.SeatStatusType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventTimeReserveSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_time_id")
    private EventTimes eventTimes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_seat_id")
    private EventSeat eventSeat;

    // 좌석 예약 상태 true일때 예약 가능
    private SeatStatusType seatReserveStatus = SeatStatusType.AVAILABLE;

    @Builder
    public EventTimeReserveSeat(EventTimes eventTimes, EventSeat eventSeat, SeatStatusType seatReserveStatus) {
        this.eventTimes = eventTimes;
        this.eventSeat = eventSeat;
        this.seatReserveStatus = seatReserveStatus;
    }

    public void setEventTimes(EventTimes eventTime) {
        this.eventTimes = eventTime;
    }

}
