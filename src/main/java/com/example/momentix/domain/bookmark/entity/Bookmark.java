package com.example.momentix.domain.bookmark.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Bookmarks")
@NoArgsConstructor
public class Bookmark extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId", nullable = false)
    private Events events;

    @Column(nullable = false)
    private boolean bookmarkStatus = false; // 기본값 false

    //== 생성자 ==//
    public Bookmark(Users users, Events events) {
        this.users = users;
        this.events = events;
        this.bookmarkStatus = true; // 처음 생성될 때는 항상 true
    }

    //== 비즈니스 로직 (상태 토글) ==//
    public void toggleStatus() {
        this.bookmarkStatus = !this.bookmarkStatus;
    }
}