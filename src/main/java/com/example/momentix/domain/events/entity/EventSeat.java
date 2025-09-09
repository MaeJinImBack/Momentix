package com.example.momentix.domain.events.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.enums.SeatGradeType;
import com.example.momentix.domain.events.entity.enums.SeatPartType;
import com.example.momentix.domain.events.entity.seats.Seats;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSeat extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 좌석 등급
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatGradeType seatGradeType;

    // 좌석 구역
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatPartType seatPartType;
    // 좌석 가격
    @Column(nullable = false)
    private BigDecimal seatPrice;

    @Column(nullable = false)
    private boolean seatStatus;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_id")
    private Events events;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seats_id")
    private Seats seats;

    @Builder
    public EventSeat(String seatGradeType, String seatPartType, BigDecimal seatPrice, Seats seat) {
        this.seatGradeType =  SeatGradeType.valueOf(seatGradeType);
        this.seatPartType = SeatPartType.valueOf(seatPartType);
        this.seatPrice = seatPrice;
        this.seats = seat;
    }

    public void setEvents(Events events) {
        this.events = events;
    }
}

