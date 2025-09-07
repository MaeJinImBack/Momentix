package com.example.momentix.domain.events.service;

import com.example.momentix.domain.events.dto.request.CreateEventsRequestDto;
import com.example.momentix.domain.events.dto.response.EventsResponseDto;
import com.example.momentix.domain.events.entity.EventCast;
import com.example.momentix.domain.events.entity.EventPlace;
import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.casts.Casts;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.events.entity.places.Places;
import com.example.momentix.domain.events.entity.reservationtimes.ReservationTimes;
import com.example.momentix.domain.events.repository.EventsRepository;
import com.example.momentix.domain.events.repository.casts.CastsRepository;
import com.example.momentix.domain.events.repository.eventtimes.EventTimesRepository;
import com.example.momentix.domain.events.repository.places.PlacesRepository;
import com.example.momentix.domain.events.repository.reservationtimes.ReservationTimesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class EventsService {
    private final EventsRepository eventsRepository;
    private final PlacesRepository placesRepository;
    private final EventTimesRepository eventTimesRepository;
    private final ReservationTimesRepository reservationTimesRepository;
    private final CastsRepository castsRepository;

    public EventsResponseDto createEvent(@RequestBody CreateEventsRequestDto requestDto) {

        // Events(공연) 테이블 기본 정보로 생성
        Events createEvent = Events.builder()
                .eventTitle(requestDto.getEventTitle())
                .ageRatingType(requestDto.getAgeRating())
                .eventCategoryType(requestDto.getEventCategory())
                .build();

        // Places(공연장) 공연장 정보로 검색 or 생성
        Places places = placesRepository.findByPlaceName(requestDto.getPlaceName())
                .orElseGet(() -> Places.builder()
                        .placeName(requestDto.getPlaceName())
                        .placeAddress(requestDto.getPlaceAddress())
                        .build());
        // Places(공연장) 저장(or Update)
        placesRepository.save(places);

        // EventTimes(공연 시간) 생성
        EventTimes eventTimes = EventTimes.builder()
                .eventStartTime(requestDto.getEventStartTime())
                .eventEndTime(requestDto.getEventEndTime())
                .events(createEvent)
                .build();

        // ReservationTimes(예매 시간) 생성
        ReservationTimes reservationTimes = ReservationTimes.builder()
                .reservationStartTime(requestDto.getReservationStartTime())
                .reservationEndTime(requestDto.getReservationStartTime())
                .events(createEvent)
                .build();

        // Casts(출연자) 출연자 정보로 검색 or 생성
        Casts casts = castsRepository.findByCastName(requestDto.getCastName())
                .orElseGet(() -> Casts.builder()
                        .castName(requestDto.getCastName())
                        .build());
        // Casts(출연자) 저장(or Update)
        castsRepository.save(casts);

        // EventPlace(공연, 공연장소 중간테이블), EventTime(공연시간), ReservationTime(예매시간), EventCast(공연, 출연자 중간테이블) 저장
        // 연관관계 편의 메서드 사용
        createEvent.addEventInfo(
                EventPlace.builder()
                        .events(createEvent)
                        .places(places)
                        .build(),
                eventTimes,
                reservationTimes,
                EventCast.builder()
                        .events(createEvent)
                        .casts(casts)
                        .build()
        );
        // Events(공연) 저장
        eventsRepository.save(createEvent);

        return new EventsResponseDto(createEvent, places, eventTimes, reservationTimes, casts);

    }
}
