package com.example.momentix.domain.review.service;

import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.repository.EventsRepository;
import com.example.momentix.domain.review.dto.request.CreateReviewRequestDto;
import com.example.momentix.domain.review.dto.request.UpdateReviewRequestDto;
import com.example.momentix.domain.review.dto.response.ReviewResponseDto;
import com.example.momentix.domain.review.entity.Review;
import com.example.momentix.domain.review.repository.ReviewRepository;
import com.example.momentix.domain.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventsRepository eventsRepository;

    @Transactional
    public ReviewResponseDto createReview(Long eventId, CreateReviewRequestDto requestDto, Users users) {

        validateRating(requestDto.getRating());

        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공연을 찾을 수 없습니다."));

        // TODO: 사용자가 해당 공연을 예매했는지 권한 검증 로직 추가 필요

        Review review = new Review(
                event,
                users,
                requestDto.getContents(),
                requestDto.getRating()
        );

        Review savedReview = reviewRepository.save(review);

        return new ReviewResponseDto(savedReview);
    }

    public Page<ReviewResponseDto> getReviews(Long eventId, Pageable pageable) {

        Page<Review> reviewPage = reviewRepository.findByEvents_IdAndIsDeletedFalse(eventId, pageable);

        return reviewPage.map(ReviewResponseDto::new);
    }

    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequestDto requestDto, Users user) {

        validateRating(requestDto.getRating());

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        if (!review.getUsers().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("리뷰를 수정할 권한이 없습니다.");
        }
        review.update(requestDto.getContents(), requestDto.getRating());
    }

    @Transactional
    public void deleteReview(Long reviewId, Users user) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        if (!(user.getRole().name().equals("ADMIN") ||review.getUsers().getUserId().equals(user.getUserId()))) {
            throw new AccessDeniedException("리뷰를 삭제할 권한이 없습니다.");
        }

        review.softDelete(); // Review 엔티티에 softDelete() 메소드 추가 필요
    }

    private void validateRating(Double rating) {
        // rating을 0.5로 나눈 나머지가 0이 아니면 (즉, 0.5 단위가 아니면)
        if (rating % 0.5 != 0) {
            throw new IllegalArgumentException("별점은 0.5 단위로만 입력할 수 있습니다.");
        }
    }
}