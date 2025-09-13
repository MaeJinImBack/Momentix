package com.example.momentix.domain.events.repository;

import com.example.momentix.domain.events.entity.EventPlace;
import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.places.Places;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventPlaceRepository extends JpaRepository<EventPlace, Long> {
    boolean existsByEventsAndPlaces(Events events, Places places);

    boolean existsByIdAndEventsId(Long eventPlaceId, Long eventsId);

    Optional<EventPlace> findByIdAndEventsId(Long eventPlaceId, Long eventsId);
}
