package com.example.momentix.domain.events.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.enums.AgeRatingType;
import com.example.momentix.domain.events.entity.enums.EventCategoryType;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.events.entity.reservationtimes.ReservationTimes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Table(name = "Events")
@NoArgsConstructor
@AllArgsConstructor
public class Events extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // 공연 제목
    private String eventTitle;

    // 연령 등급
    @Enumerated(EnumType.STRING)
    private AgeRatingType ageRatingType;

    // 공연 카테고리
    @Enumerated(EnumType.STRING)
    private EventCategoryType eventCategoryType;

    // 공연장
    @Builder.Default
    @OneToMany(mappedBy = "events", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventPlace> eventPlaces = new ArrayList<>();
    // 공연 시간 (시작시간, 종료시간)
    @Builder.Default
    @OneToMany(mappedBy = "events", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventTimes> eventTimes = new ArrayList<>();
    // 공연 예매 시간 (시작시간, 종료시간)
    @Builder.Default
    @OneToMany(mappedBy = "events", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationTimes> reservationTimes = new ArrayList<>();
    // 공연 출연자
    @Builder.Default
    @OneToMany(mappedBy = "events", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventCast> eventCasts = new ArrayList<>();

    @Column(nullable = false)
    private boolean isDeleted;


    public void addEventInfo(EventPlace eventPlace, EventTimes eventTime, ReservationTimes reservationTime, EventCast eventCast) {
        eventPlaces.add(eventPlace);
        eventPlace.setEvents(this);

        eventTimes.add(eventTime);
        eventTime.setEvents(this);

        reservationTimes.add(reservationTime);
        reservationTime.setEvents(this);

        eventCasts.add(eventCast);
        eventPlace.setEvents(this);
    }

}
