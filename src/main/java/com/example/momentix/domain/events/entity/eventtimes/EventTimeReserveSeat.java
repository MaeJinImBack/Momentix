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
@Table(
        name = "event_time_reserve_seat",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_event_time_seat",
                columnNames = {"event_time_id", "event_seat_id"}
        )
)
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

    public boolean isAvailable() {
        return this.seatReserveStatus == SeatStatusType.AVAILABLE;
    }

    public void hold() {
        if (!isAvailable()) {
            throw new IllegalStateException("현재 상태(" + seatReserveStatus + ")에서는 HOLD 불가");
        }
        this.seatReserveStatus = SeatStatusType.HOLD;
    }

    public void release() {
        if (this.seatReserveStatus != SeatStatusType.HOLD) {
            throw new IllegalStateException("HOLD 상태가 아니라 해제 불가");
        }
        this.seatReserveStatus = SeatStatusType.AVAILABLE;
    }
}
