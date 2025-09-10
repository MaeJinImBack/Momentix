package com.example.momentix.domain.reservation.service;


import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.repository.EventsRepository;
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

    @Transactional
    public ReservationResponseDto selectEvent(Long userId, Long eventId) {

        if(!usersRepository.existsById(userId)){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if(!eventsRepository.existsById(eventId)){
            throw new IllegalArgumentException("존재하지 않는 공연입니다.");
        }

        Users user = usersRepository.getReferenceById(userId);
        Events event = eventsRepository.getReferenceById(eventId);

        List<Reservations> reservationsList = reservationsRepository.findActiveByUsersAndEvents(userId, eventId
                ,List.of(
                        ReservationStatusType.DRAFT,
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
}
