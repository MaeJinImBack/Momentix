package com.example.momentix.domain.review.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Reviews")
@NoArgsConstructor
public class Review extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId", nullable = false)
    private Events events;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Users users;

    @Column(nullable = false, length = 500)
    private String contents;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private boolean isDeleted = false;

    //== 생성자 ==//
    public Review(Events events, Users users, String contents, Double rating) {
        this.events = events;
        this.users = users;
        this.contents = contents;
        this.rating = rating;
    }
}