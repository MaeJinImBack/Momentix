package com.example.momentix.domain.reservation.service;


import com.example.momentix.domain.events.entity.EventPlace;
import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.events.repository.EventPlaceRepository;
import com.example.momentix.domain.events.repository.EventsRepository;
import com.example.momentix.domain.events.repository.eventtimes.EventTimesRepository;
import com.example.momentix.domain.reservation.dto.ReservationResponseDto;
import com.example.momentix.domain.reservation.entitiy.ReservationStatusType;
import com.example.momentix.domain.reservation.entitiy.Reservations;
import com.example.momentix.domain.reservation.repository.ReservationRepository;
import com.example.momentix.domain.users.entity.Users;
import com.example.momentix.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationsRepository;

    private final UserRepository usersRepository;

    private final EventsRepository eventsRepository;

    private final EventPlaceRepository eventPlaceRepository;

    private final EventTimesRepository eventTimesRepository;

    @Transactional
    public ReservationResponseDto selectEvent(Long userId, Long eventId) {


        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));


        List<Reservations> reservationsList = reservationsRepository.findActiveByUsers_UsersIdAndEvents_Id(userId, eventId
                ,List.of(
                        ReservationStatusType.DRAFT,
                        ReservationStatusType.SELECT_PLACE,
                        ReservationStatusType.SELECT_TIME,
                        ReservationStatusType.SELECT_SEAT,
                        ReservationStatusType.WAIT_PAYMENT
                ));

        if(! reservationsList.isEmpty()){
            return ReservationResponseDto.from(reservationsList.get(0));
        }

        Reservations reservation = Reservations.builder()
                .users(user)
                .events(event)
                .reservationStatusType(ReservationStatusType.DRAFT)
                .build();

        reservationsRepository.save(reservation);

        return ReservationResponseDto.from(reservation);
    }

    @Transactional
    public ReservationResponseDto selectEventPlace(Long userId, Long reservationId, Long eventPlaceId) {

        if(!usersRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        Reservations reservations = reservationsRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));

        if(!reservations.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        if(reservations.getEvents() == null){
            throw new IllegalArgumentException("공연이 먼저 선택되어야 합니다.");
        }

        switch (reservations.getReservationStatusType()) {

            case DRAFT, SELECT_PLACE-> {}

            default -> throw new IllegalArgumentException("공연장 선택이 불가능합니다.");
        }

        Long eventsId  = reservations.getEvents().getId();


        boolean flag = eventPlaceRepository.existsByIdAndEventsId(eventPlaceId, eventsId);

        if(!flag) {
            throw new IllegalArgumentException("해당 공연의 공연 장소가 없습니다.");
        }

        EventPlace eventPlace = eventPlaceRepository.getReferenceById(eventPlaceId);

        reservations.selectEventPlace(eventPlace);

        return ReservationResponseDto.from(reservations);
    }


    @Transactional
    public ReservationResponseDto selectEventTime(Long userId, Long reservationId, Long eventTimeId) {

        if(!usersRepository.existsById(userId)){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        Reservations reservations = reservationsRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));

        if(!reservations.getUsers().getUserId().equals(userId)){
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        if(reservations.getEvents() == null){
            throw new IllegalArgumentException("공연이 먼저 선택되어야 합니다.");
        }

        switch (reservations.getReservationStatusType()) {

            case DRAFT,SELECT_SEAT, WAIT_PAYMENT, CANCELED ,COMPLETED_TICKET
                    -> throw new IllegalArgumentException("시간 선택이 불가능합니다.");
        }

        Long eventsId = reservations.getEvents().getId();

        boolean flag = eventTimesRepository.existsByIdAndEventsId(eventTimeId, eventsId);

        if(!flag){
            throw new IllegalArgumentException("해당 공연에 공연 시간이 없습니다.");
        }

        EventTimes eventTimes = eventTimesRepository.getReferenceById(eventTimeId);

        reservations.selectEventTime(eventTimes);

        return ReservationResponseDto.from(reservations);

    }


}
