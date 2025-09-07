package com.example.momentix.domain.events.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.casts.Casts;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
// 공연과 출연진 중간테이블
public class EventCast extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn()
    private Events events;

    @ManyToOne()
    @JoinColumn()
    private Casts casts;

    @Builder
    public EventCast(Events events, Casts casts) {
        this.events = events;
        this.casts = casts;
    }

    public void setEvents(Events events) {
        this.events = events;
    }
}
