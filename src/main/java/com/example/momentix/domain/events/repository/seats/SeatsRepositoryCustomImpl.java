package com.example.momentix.domain.events.repository.seats;


import com.example.momentix.domain.events.dto.response.BaseSeatResponseDto;
import com.example.momentix.domain.events.entity.seats.QSeats;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

public class SeatsRepositoryCustomImpl implements SeatsRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    public SeatsRepositoryCustomImpl(JPAQueryFactory queryFactory, EntityManager entityManager) {
        this.queryFactory = queryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public void softDeleteSeatByList(List<BaseSeatResponseDto> softDeleteList) {
        QSeats seat = QSeats.seats;
        queryFactory
                .update(seat)
                .set(seat.isDeleted, true)
                .where(softDeleteList.stream()
                        .map(baseSeatResponseDto ->
                                seat.seatRow.eq(baseSeatResponseDto.getSeatRow())
                                        .and(seat.seatCol.eq(baseSeatResponseDto.getSeatCol()))
                        ).reduce(BooleanExpression::or)
                        .orElseThrow(() -> new IllegalArgumentException("좌표 없음")))
                .execute();

        // Bulk Update 후 동기화
        entityManager.flush();
        entityManager.clear();
    }


}
