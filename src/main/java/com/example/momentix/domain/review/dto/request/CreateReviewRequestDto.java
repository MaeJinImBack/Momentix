package com.example.momentix.domain.review.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateReviewRequestDto {
    private String contents;
    private Double rating;
}