package com.example.momentix.domain.events.repository.eventimages;

import com.example.momentix.domain.events.entity.eventimages.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventImagesRepository extends JpaRepository<EventImage, Long> {
}
