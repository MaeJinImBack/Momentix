package com.example.momentix.domain.events.repository;

import com.example.momentix.domain.events.dto.response.AllReadEventsResponseDto;
import com.example.momentix.domain.events.entity.Events;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long>, EventsRepositoryCustom {
    @Query ("SELECT new com.example.momentix.domain.events.dto.response.AllReadEventsResponseDto(" +
            "e.eventTitle," +
            "e.eventCategoryType," +
            "e.eventStartDate," +
            "e.eventEndDate," +
            "p.placeName) " +
            "FROM Events e " +
            "JOIN EventPlace ep ON e.id = ep.events.id " +
            "JOIN ep.places p ")
    List<AllReadEventsResponseDto> AllReadEvents();


}
