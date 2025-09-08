package com.example.momentix.domain.events.entity.places;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.EventPlace;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Places extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 공연장 이름
    @Column(nullable = false)
    private String placeName;

    // 주소
    @Column(nullable = false)
    private String placeAddress;

    // 공연
    @OneToMany(mappedBy = "places")
    private List<EventPlace> eventPlace;

    @Builder
    public Places(String placeName, String placeAddress) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
    }
}
