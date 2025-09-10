package com.example.momentix.domain.events.repository.eventimages;

import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.eventimages.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventImagesRepository extends JpaRepository<EventImage, Long> {
    Optional<EventImage> findByEvents(Events event);
}