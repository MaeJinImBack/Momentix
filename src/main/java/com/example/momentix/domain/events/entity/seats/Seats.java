package com.example.momentix.domain.events.entity.seats;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.enums.SeatGradeType;
import com.example.momentix.domain.events.entity.enums.SeatPartType;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
// 공연 좌석
public class Seats extends TimeStamped {
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

    // 좌석 행 열
    @Column(nullable = false)
    private int x;
    @Column(nullable = false)
    private int y;

    // 좌석 가격
    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean seatStatus;


}
