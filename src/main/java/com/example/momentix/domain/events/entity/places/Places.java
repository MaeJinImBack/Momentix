package com.example.momentix.domain.events.entity.places;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.EventPlace;
import com.example.momentix.domain.events.entity.seats.Seats;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Places extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공연장 이름
    @Column(nullable = false)
    private String placeName;

    // 주소
    @Column(nullable = false)
    private String placeAddress;

    // 공연
    @OneToMany(mappedBy = "places")
    private List<EventPlace> eventPlaceList;

    @OneToMany(mappedBy = "places", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seats> seatList;

    @Builder
    public Places(String placeName, String placeAddress) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
    }

    public void addSeats(Seats seats) {
        this.seatList.add(seats);
        seats.setPlaces(this);
    }
}
