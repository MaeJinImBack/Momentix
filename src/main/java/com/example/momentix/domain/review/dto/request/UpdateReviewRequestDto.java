package com.example.momentix.domain.review.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReviewRequestDto {

    @NotBlank(message = "리뷰 내용은 비워둘 수 없습니다.")
    @Size(max = 500, message = "리뷰는 500자를 초과할 수 없습니다.")
    private String contents;

    @DecimalMin(value = "0.5", message = "별점은 0.5 이상이어야 합니다.")
    @DecimalMax(value = "5.0", message = "별점은 5.0 이하이어야 합니다.")
    private Double rating;
}
