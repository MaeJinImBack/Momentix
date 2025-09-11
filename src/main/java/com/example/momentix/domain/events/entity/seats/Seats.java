package com.example.momentix.domain.events.entity.seats;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.places.Places;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
// 공연 좌석
public class Seats extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "places_id")
    private Places places;

    // 좌석 행 열
    @Column(nullable = false)
    private int seatRow;
    @Column(nullable = false)
    private int seatCol;


    @Column(nullable = false)
    private boolean isDeleted = false;

    @Builder
    public Seats(int seatRow, int seatCol) {
        this.seatRow = seatRow;
        this.seatCol = seatCol;
    }

    public void setPlaces(Places places) {
        this.places = places;
    }

}
