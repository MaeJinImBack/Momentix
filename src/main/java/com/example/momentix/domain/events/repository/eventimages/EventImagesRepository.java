package com.example.momentix.domain.events.repository.eventimages;

import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.eventimages.EventImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventImagesRepository extends JpaRepository<EventImages, Long> {
    Optional<EventImages> findByEvents(Events event);
}