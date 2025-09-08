package com.example.momentix.domain.events.entity.reservationtimes;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.Events;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 예매 시간
public class ReservationTimes extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예매 시작 시간
    @Column(nullable = false)
    private LocalDateTime reservationStartTime;

    // 예매 종료 시간
    @Column(nullable = false)
    private LocalDateTime reservationEndTime;

    // 공연
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="events_id", nullable = false)
    private Events events;

    @Builder
    public ReservationTimes(LocalDateTime reservationStartTime, LocalDateTime reservationEndTime, Events events) {
        this.reservationStartTime = reservationStartTime;
        this.reservationEndTime = reservationEndTime;
        this.events = events;
    }

    public void setEvents(Events events){
        this.events = events;
    }

}
