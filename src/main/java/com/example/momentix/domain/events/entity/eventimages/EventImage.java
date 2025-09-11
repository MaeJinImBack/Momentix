package com.example.momentix.domain.events.entity.eventimages;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.Events;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "EventImages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)

// 공연 관련 이미지
public class EventImage extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventImageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Events events;

    @Column(columnDefinition = "TEXT")
    private String posterImageUrl;

    @Column(columnDefinition = "TEXT")
    private String detailImageUrl;

    public EventImage(Events events) {
        this.events = events;
    }

    public void updatePosterImageUrl(String posterImageUrl) {
        this.posterImageUrl = posterImageUrl;
    }

    public void updateDetailImageUrl(String detailImageUrl) {
        this.detailImageUrl = detailImageUrl;
    }
}