package com.example.momentix.domain.events.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.places.Places;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 공연과 장소 중간 테이블
public class EventPlace extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name="events_id")
    private Events events;

    @ManyToOne
    @JoinColumn(name="places_id")
    private Places places;

    @Builder
    public EventPlace(Events events, Places places) {
        this.events = events;
        this.places = places;
    }

    public void setEvents(Events events){
        this.events = events;
    }
}
