package com.example.momentix.domain.events.repository;

import com.example.momentix.domain.events.dto.response.SeatResponseDto;
import com.example.momentix.domain.events.entity.QEventSeat;
import com.example.momentix.domain.events.entity.enums.SeatGradeType;
import com.example.momentix.domain.events.entity.enums.SeatPartType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

public class EventSeatRepositoryCustomImpl implements EventSeatRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    public EventSeatRepositoryCustomImpl(JPAQueryFactory queryFactory, EntityManager entityManager) {
        this.queryFactory = queryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public void updateEventSeatListByEventsIdAndPlaceId(Long eventsId, Long placeId, List<SeatResponseDto> seatList) {
        QEventSeat eventSeat = QEventSeat.eventSeat;

        for (SeatResponseDto updateSeat : seatList) {
            queryFactory
                    .update(eventSeat)
                    .set(eventSeat.seatGradeType, SeatGradeType.valueOf(updateSeat.getSeatGradeType()))
                    .set(eventSeat.seatPrice, updateSeat.getSeatPrice())
                    .set(eventSeat.seatPartType, SeatPartType.valueOf(updateSeat.getSeatPartType()))
                    .where(eventSeat.events.id.eq(eventsId)
                            .and(eventSeat.seats.seatRow.eq(updateSeat.getSeatRow()))
                            .and(eventSeat.seats.seatCol.eq(updateSeat.getSeatCol()))
                            .and(eventSeat.seats.places.id.eq(placeId)))
                    .execute();
        }
        // Update 후 동기화
        entityManager.flush();
        entityManager.clear();
    }
}
