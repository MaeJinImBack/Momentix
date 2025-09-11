package com.example.momentix.domain.events.repository.eventtimes;

import com.example.momentix.domain.events.dto.request.SearchSeatRequestDto;
import com.example.momentix.domain.events.dto.response.ReserveSeatResponseDto;
import com.example.momentix.domain.events.entity.QEventSeat;
import com.example.momentix.domain.events.entity.eventtimes.QEventTimeReserveSeat;
import com.example.momentix.domain.events.entity.eventtimes.QEventTimes;
import com.example.momentix.domain.events.entity.seats.QSeats;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class EventTimeReserveSeatRepositoryCustomImpl implements EventTimeReserveSeatRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public EventTimeReserveSeatRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ReserveSeatResponseDto> searchAllSeat(SearchSeatRequestDto requestDto, Pageable pageable) {
        QSeats s = QSeats.seats;
        QEventTimeReserveSeat etrs = QEventTimeReserveSeat.eventTimeReserveSeat;
        QEventSeat es = QEventSeat.eventSeat;
        QEventTimes et = QEventTimes.eventTimes;

        List<ReserveSeatResponseDto> seatResponse = queryFactory
                .select(Projections.constructor(ReserveSeatResponseDto.class,
                        s.id,
                        es.seatGradeType,
                        es.seatPartType,
                        es.seatPrice,
                        etrs.seatReserveStatus
                ))
                .from(etrs)
                .join(etrs.eventSeat, es)
                .join(es.seats, s)
                .join(etrs.eventTimes, et)
                .where(
                        etrs.eventTimes.id.eq(requestDto.getEventTimeId()),
                        et.events.id.eq(requestDto.getEventId()),
                        es.events.id.eq(requestDto.getEventId()),
                        s.places.id.eq(requestDto.getPlaceId()),
                        es.seats.id.eq(s.id)
                )
                .offset(pageable.getOffset())   // (2) 페이지 번호
                .limit(pageable.getPageSize())  // (3) 페이지 사이즈
                .fetch();


        return new PageImpl<>(seatResponse, pageable, seatResponse.size());

    }
}
