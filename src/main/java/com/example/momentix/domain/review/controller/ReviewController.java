package com.example.momentix.domain.review.controller;

import com.example.momentix.domain.auth.impl.UserDetailsImpl;
import com.example.momentix.domain.review.dto.request.CreateReviewRequestDto;
import com.example.momentix.domain.review.dto.response.ReviewResponseDto;
import com.example.momentix.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/events/{eventId}")
    public ResponseEntity<ReviewResponseDto> createReview(
            @PathVariable Long eventId,
            @RequestBody CreateReviewRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ReviewResponseDto response = reviewService.createReview(eventId, requestDto, userDetails.getUser());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}