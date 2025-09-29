package com.example.momentix.domain.review.repository;

import com.example.momentix.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByEvents_IdAndIsDeletedFalse(Long eventId, Pageable pageable);
}
