package com.example.momentix.domain.events.entity.casts;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.EventCast;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Casts extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 출연자 이름
    @Column(nullable = false, length = 20)
    private String castName;
    ;
    // 출연자 이미지
    @Column(length = 255)
    private String castImageUrl;

    @OneToMany(mappedBy = "casts")
    private List<EventCast> eventCastList;

    @Builder
    public Casts(String castName, String castImageUrl) {
        this.castName = castName;
        this.castImageUrl = castImageUrl;
    }

}
