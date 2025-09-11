package com.example.momentix.domain.review.dto.response;

import com.example.momentix.domain.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {
    private Long reviewId;
    private String contents;
    private Double rating;
    private String authorNickname;
    private LocalDateTime createdAt;

    public ReviewResponseDto(Review review) {
        this.reviewId = review.getReviewId();
        this.contents = review.getContents();
        this.rating = review.getRating();
        this.authorNickname = review.getUsers().getNickname();
        this.createdAt = review.getCreatedAt();
    }
}