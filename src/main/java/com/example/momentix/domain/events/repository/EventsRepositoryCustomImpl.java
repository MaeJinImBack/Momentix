package com.example.momentix.domain.events.repository;

import com.example.momentix.domain.events.dto.response.EventTimeResponseDto;
import com.example.momentix.domain.events.dto.response.ReadCastResponseDto;
import com.example.momentix.domain.events.dto.response.ReadEventResponseDto;
import com.example.momentix.domain.events.dto.response.ReservationTimeResponseDto;
import com.example.momentix.domain.events.entity.QEventCast;
import com.example.momentix.domain.events.entity.QEventPlace;
import com.example.momentix.domain.events.entity.QEvents;
import com.example.momentix.domain.events.entity.casts.QCasts;
import com.example.momentix.domain.events.entity.enums.EventCategoryType;
import com.example.momentix.domain.events.entity.eventtimes.QEventTimes;
import com.example.momentix.domain.events.entity.places.QPlaces;
import com.example.momentix.domain.events.entity.reservationtimes.QReservationTimes;
import com.example.momentix.domain.search.dto.SearchRequestDto;
import com.example.momentix.domain.search.dto.SearchResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
                                .and(places.id.eq(placeId))
                                .and(events.isDeleted.eq(false)))
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

    // 카테고리 입력 받고 카테고리 같을 때만 True
    public BooleanExpression categoryType(EventCategoryType eventCategory) {
        return eventCategory != null ? QEvents.events.eventCategoryType.eq(eventCategory) : null;
    }

    // 지역 입력받고 Address에서 시작값에 들어갈 때만 True
    public BooleanExpression regionAddress(String region) {
        return region != null ? QPlaces.places.placeAddress.startsWith(region) : null;
    }

    // 공연 제목이 포함되어 있을 경우에만 True
    public BooleanExpression title(String eventTitle) {
        return eventTitle != null ? QEvents.events.eventTitle.contains(eventTitle) : null;
    }

    // 기간별로 True 반환
    public BooleanExpression period(LocalDate eventStartDate, LocalDate eventEndDate) {
        // 시작 날짜 종료 날짜 (기간으로 검색시) 검색 기간과 이벤트 기간이 겹치는 이벤트 조회
        if (eventStartDate != null && eventEndDate != null) {
            return QEvents.events.eventEndDate.goe(eventStartDate).and(QEvents.events.eventStartDate.loe(eventEndDate));
            // 시작일 검색시 이벤트 종료일이 검색 시작일 이후인 이벤트 조회
        } else if (eventStartDate != null) {
            return QEvents.events.eventEndDate.goe(eventStartDate);
            // 종료일 검색시 이벤트 시작일이 검색 종료일 이전인 이벤트 조회
        } else if (eventEndDate != null) {
            return QEvents.events.eventStartDate.loe(eventEndDate);
        } else {
            return null;
        }
    }

    public Page<SearchResponseDto> searchEventByParam(SearchRequestDto searchRequestDto, Pageable pageable) {
        QEvents events = QEvents.events;
        QPlaces places = QPlaces.places;
        QEventPlace eventPlace = QEventPlace.eventPlace;

        List<SearchResponseDto> responseDto = queryFactory
                .select(Projections.bean(SearchResponseDto.class,
                        events.eventTitle.as("eventTitle"),
                        places.placeName.as("placeName"),
                        events.eventStartDate.as("eventStartDate"),
                        events.eventEndDate.as("eventEndDate")))
                .from(events)
                .leftJoin(events.eventPlaceList, eventPlace)
                .leftJoin(eventPlace.places, places)
                .where(
                        events.isDeleted.eq(false),
                        categoryType(searchRequestDto.getEventCategory()),
                        regionAddress(searchRequestDto.getRegion()),
                        title(searchRequestDto.getEventTitle()),
                        period(searchRequestDto.getSearchStartDate(), searchRequestDto.getSearchEndDate())
                )
                .offset(pageable.getOffset())   // (2) 페이지 번호
                .limit(pageable.getPageSize())  // (3) 페이지 사이즈
                .orderBy(QEvents.events.eventStartDate.desc())
                .fetch();

        // page total값
        long total = Optional.ofNullable(queryFactory
                .select(events.count())
                .from(events)
                .join(events.eventPlaceList, eventPlace)
                .leftJoin(eventPlace.places, places)
                .where(
                        events.isDeleted.eq(false),
                        categoryType(searchRequestDto.getEventCategory()),
                        regionAddress(searchRequestDto.getRegion()),
                        title(searchRequestDto.getEventTitle()),
                        period(searchRequestDto.getSearchStartDate(), searchRequestDto.getSearchEndDate())
                )
                .fetchOne()).orElse(0L);

        return new PageImpl<>(responseDto, pageable, total);
    }
}
