package com.example.momentix.domain.events.entity.eventtimes;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.Events;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 공연 시간
public class EventTimes extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공연 시작 시간
    @Column(nullable = false)
    private LocalDateTime eventStartTime;

    // 공연 종료 시간
    @Column(nullable = false)
    private LocalDateTime eventEndTime;

    // 공연
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="events_id", nullable = false)
    private Events events;

    // 공연 시간별 좌석 예매 현황
    @OneToMany(mappedBy = "eventTime", cascade =  CascadeType.ALL, orphanRemoval = true)
    private List<EventTimeReservationSeat> eventTimeReservationSeatList;

    @Builder
    public EventTimes(LocalDateTime eventStartTime, LocalDateTime eventEndTime, Events events) {
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.events = events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }


}
