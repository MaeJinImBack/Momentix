package com.example.momentix.domain.events.repository;

import com.example.momentix.domain.events.dto.response.EventTimeResponseDto;
import com.example.momentix.domain.events.dto.response.ReadCastResponseDto;
import com.example.momentix.domain.events.dto.response.ReadEventResponseDto;
import com.example.momentix.domain.events.dto.response.ReservationTimeResponseDto;
import com.example.momentix.domain.events.entity.QEventCast;
import com.example.momentix.domain.events.entity.QEventPlace;
import com.example.momentix.domain.events.entity.QEvents;
import com.example.momentix.domain.events.entity.casts.QCasts;
import com.example.momentix.domain.events.entity.eventtimes.QEventTimes;
import com.example.momentix.domain.events.entity.places.QPlaces;
import com.example.momentix.domain.events.entity.reservationtimes.QReservationTimes;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class EventsRepositoryCustomImpl implements EventsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public EventsRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public ReadEventResponseDto searchEventById(Long eventId, Long placeId) {
        QEvents events = QEvents.events;
        QPlaces places = QPlaces.places;
        QEventPlace eventPlace = QEventPlace.eventPlace;
        QEventCast eventCast = QEventCast.eventCast;
        QCasts casts = QCasts.casts;
        QEventTimes eventTimes = QEventTimes.eventTimes;
        QReservationTimes reservationTimes = QReservationTimes.reservationTimes;

        ReadEventResponseDto readEvent = queryFactory
                .select(Projections.bean(ReadEventResponseDto.class,
                        events.id,
                        events.eventTitle,
                        events.eventCategoryType,
                        events.ageRatingType,
                        places.placeName,
                        places.placeAddress,
                        events.eventStartDate,
                        events.eventEndDate
                ))
                .from(events)
                .join(events.eventPlaceList, eventPlace)
                .join(eventPlace.places, places)
                .where(
                        events.id.eq(eventId)
                                .and(places.id.eq(placeId)))
                .fetchOne();

        List<EventTimeResponseDto> eventTimeDtoList = queryFactory
                .select(Projections.constructor(EventTimeResponseDto.class,
                        eventTimes.eventStartTime,
                        eventTimes.eventEndTime
                ))
                .from(eventTimes)
                .where(eventTimes.events.id.eq(eventId))
                .fetch();
        List<ReservationTimeResponseDto> reservationTimeDtoList = queryFactory
                .select(Projections.constructor(ReservationTimeResponseDto.class,
                        reservationTimes.reservationStartTime,
                        reservationTimes.reservationEndTime))
                .from(reservationTimes)
                .where(reservationTimes.events.id.eq(eventId))
                .fetch();

        List<ReadCastResponseDto> readCastResponseDtoList = queryFactory
                .select(Projections.constructor(ReadCastResponseDto.class,
                        casts.castName,
                        casts.castImageUrl))
                .from(eventCast)
                .join(eventCast.casts, casts)
                .where(eventCast.events.id.eq(eventId))
                .fetch();
        readEvent.setEventTimeResponseDtoList(eventTimeDtoList);
        readEvent.setReservationTimeResponseDtoList(reservationTimeDtoList);
        readEvent.setReadCastResponseDtoList(readCastResponseDtoList);

        return readEvent;
    }

}
