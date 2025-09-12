package com.example.momentix.domain.reservation.service;


import com.example.momentix.domain.events.entity.EventPlace;
import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.eventtimes.EventTimeReserveSeat;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.events.repository.EventPlaceRepository;
import com.example.momentix.domain.events.repository.EventSeatRepository;
import com.example.momentix.domain.events.repository.EventsRepository;
import com.example.momentix.domain.events.repository.eventtimes.EventTimeReserveSeatRepository;
import com.example.momentix.domain.events.repository.eventtimes.EventTimesRepository;
import com.example.momentix.domain.reservation.dto.ReservationResponseDto;
import com.example.momentix.domain.reservation.entity.ReservationStatusType;
import com.example.momentix.domain.reservation.entity.Reservations;
import com.example.momentix.domain.reservation.entity.SeatStatusType;
import com.example.momentix.domain.reservation.repository.ReservationRepository;
import com.example.momentix.domain.users.entity.Users;
import com.example.momentix.domain.users.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final EventTimeReserveSeatRepository eventTimeReserveSeatRepository;

    private final EventSeatRepository eventSeatRepository;


    @PersistenceContext
    private EntityManager em;

    //공연 선택
    @Transactional
    public ReservationResponseDto selectEvent(Long userId, Long eventId) {

        //이용자와 공연 존재 확인
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));

        //해당 상태의 예매 상태가 있는지 조회
        List<Reservations> reservationsList = reservationsRepository.findActiveByUsers_UsersIdAndEvents_Id(userId, eventId
                ,List.of(
                        ReservationStatusType.DRAFT,
                        ReservationStatusType.SELECT_PLACE,
                        ReservationStatusType.SELECT_TIME,
                        ReservationStatusType.SELECT_SEAT,
                        ReservationStatusType.WAIT_PAYMENT
                ));

        //있다면, list 중 1번째 가져오기
        if(! reservationsList.isEmpty()){
            return ReservationResponseDto.from(reservationsList.get(0));
        }

        //없을 시, 생성
        Reservations reservation = Reservations.builder()
                .users(user)
                .events(event)
                .reservationStatusType(ReservationStatusType.DRAFT)
                .build();

        reservationsRepository.save(reservation);

        return ReservationResponseDto.from(reservation);
    }


    //공연 장소 선택하기
    @Transactional
    public ReservationResponseDto selectEventPlace(Long userId, Long reservationId, Long eventPlaceId) {

        //사용자 확인
        if(!usersRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        //해당 예매 아이디 확인
        Reservations reservations = reservationsRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));


        //예매 대기가 본인이 아닐경우
        if(!reservations.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        //공연을 선택하지 않았을 경우
        if(reservations.getEvents() == null){
            throw new IllegalArgumentException("공연이 먼저 선택되어야 합니다.");
        }


        //공연 선택 및 공연을 선택했을 경우 제외 선택 불가
        switch (reservations.getReservationStatusType()) {

            case DRAFT, SELECT_PLACE-> {}

            default -> throw new IllegalArgumentException("공연장 선택이 불가능합니다.");
        }


        //공연 아이디를 가져와서
        Long eventsId  = reservations.getEvents().getId();


        //해당 공연이 공연 장소와 일치하는지
        boolean flag = eventPlaceRepository.existsByIdAndEventsId(eventPlaceId, eventsId);

        if(!flag) {
            throw new IllegalArgumentException("해당 공연의 공연 장소가 없습니다.");
        }

        //해당 예매 테이블에 장소 등록
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

        //공연 장소 선택 확인
        if(reservations.getEventPlace() == null){
            throw new IllegalArgumentException("공연 장소가 선택되지 않았습니다.");
        }


        //해당 상태에는 시간 선택이 불가능 -> 추후 상태 관리를 정확히 명시하여, if(==null) 로 처리하던 부분 상태로 변경
        switch (reservations.getReservationStatusType()) {
            //SElECT_TIME 일 때는 CREATE , SELECT_SEAT 일 때는 UPDATE
            case SELECT_TIME, SELECT_SEAT -> {}
            default -> throw new IllegalArgumentException("시간 선택이 불가능합니다.");
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

    //Reservation을 임시 테이블 처럼 사용하기 때문에, 티켓(예매 내역)이 생성되는 순간 제거 -> CreateTicket 에 RS.deleteReservation 추가

    //Propagation.REQUIRED 가 default 지만, 티켓 생성 트랜잭션에 사용할 예정이라 명시
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteReservation(Long userId, Long reservationId) {

        if(!usersRepository.existsById(userId)){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        Reservations reservations = reservationsRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));

        if(!reservations.getUsers().getUserId().equals(userId)){
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }


        reservationsRepository.deleteById(reservationId);

    }

    // 좌석 선택 (좌석 상태: AVAILABLE -> HOLD)
    @Transactional
    public ReservationResponseDto selectEventSeat(Long userId, Long reservationId, Long eventSeatId) {

        if (!usersRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        Reservations r = reservationsRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));
        if (!r.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        if (r.getEvents() == null)      throw new IllegalArgumentException("공연이 먼저 선택되어야 합니다.");
        if (r.getEventPlace() == null)  throw new IllegalArgumentException("공연 장소가 선택되지 않았습니다.");
        if (r.getEventTimes() == null)  throw new IllegalArgumentException("공연 시간이 선택되지 않았습니다.");

        switch (r.getReservationStatusType()) {
            case SELECT_TIME, SELECT_SEAT -> {}
            default -> throw new IllegalArgumentException("좌석 선택이 불가능한 상태입니다.");
        }

        if (!eventSeatRepository.existsById(eventSeatId)) {
            throw new IllegalArgumentException("존재하지 않는 좌석입니다.");
        }

        Long eventTimeId = r.getEventTimes().getId();

        EventTimeReserveSeat row = eventTimeReserveSeatRepository
                .findByEventTimes_Id(eventTimeId, eventSeatId)
                .orElseGet(() -> {
                    EventTimeReserveSeat created = EventTimeReserveSeat.builder()
                            .eventTimes(eventTimesRepository.getReferenceById(eventTimeId))
                            .eventSeat(eventSeatRepository.getReferenceById(eventSeatId))
                            .seatReserveStatus(SeatStatusType.AVAILABLE)
                            .build();
                    try {
                        return eventTimeReserveSeatRepository.saveAndFlush(created);
                    } catch (DataIntegrityViolationException dup) {
                        // 유니크 충돌 → 이미 누가 생성 → 다시 조회
                        return eventTimeReserveSeatRepository
                                .findByEventTimes_Id(eventTimeId, eventSeatId)
                                .orElseThrow();
                    }
                });

        // 2) CAS: AVAILABLE일 때만 HOLD로 전이
        if (!row.isAvailable()) {
            throw new IllegalStateException("이미 선점(HOLD)되었거나 선택 불가한 좌석입니다.");
        }
        row.hold();

        try {
            eventTimeReserveSeatRepository.saveAndFlush(row);
        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            // 경쟁에서 짐
            throw new IllegalStateException("이미 선점(HOLD)되었거나 선택 불가한 좌석입니다.");
        }

        // 3) 예매 객체에 좌석 반영
        var seatRef = eventSeatRepository.getReferenceById(eventSeatId);
        r.selectEventSeat(seatRef);

        return ReservationResponseDto.from(r);
    }
}
