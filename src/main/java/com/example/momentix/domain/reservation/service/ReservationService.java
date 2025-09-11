package com.example.momentix.domain.reservation.service;


import com.example.momentix.domain.events.entity.EventPlace;
import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.events.repository.EventPlaceRepository;
import com.example.momentix.domain.events.repository.EventSeatRepository;
import com.example.momentix.domain.events.repository.EventsRepository;
import com.example.momentix.domain.events.repository.eventtimes.EventTimesRepository;
import com.example.momentix.domain.reservation.dto.ReservationResponseDto;
import com.example.momentix.domain.reservation.entity.ReservationStatusType;
import com.example.momentix.domain.reservation.entity.Reservations;
import com.example.momentix.domain.reservation.repository.ReservationRepository;
import com.example.momentix.domain.users.entity.Users;
import com.example.momentix.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    private final EventSeatRepository eventSeatRepository;

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

    // 좌석 선택
    @Transactional
    public ReservationResponseDto selectEventSeat(Long userId, Long reservationId, Long eventSeatId) {

        // 사용자 존재 검증
        if (!usersRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 예약 조회 + 소유자 검증
        Reservations r = reservationsRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));
        if (!r.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        // 선행 단계 검증
        if (r.getEvents() == null)      throw new IllegalArgumentException("공연이 먼저 선택되어야 합니다.");
        if (r.getEventPlace() == null)  throw new IllegalArgumentException("공연 장소가 선택되지 않았습니다.");
        if (r.getEventTimes() == null)  throw new IllegalArgumentException("공연 시간이 선택되지 않았습니다.");

        // 상태 검증: 좌석선택 가능한 상태만 허용
        switch (r.getReservationStatusType()) {
            case SELECT_TIME, SELECT_SEAT -> { /* 허용 (좌석 변경 허용 시 SELECT_SEAT도 허용) */ }
            default -> throw new IllegalArgumentException("좌석 선택이 불가능한 상태입니다.");
        }

        // 좌석 유효성(최소한 존재 여부)
        if (!eventSeatRepository.existsById(eventSeatId)) {
            throw new IllegalArgumentException("존재하지 않는 좌석입니다.");
        }
        // TODO: 좌석이 해당 공연/장소/시간에 속하는지 검증 로직 추가
        // 예: eventSeatRepository.existsByIdAndEventsId(eventSeatId, r.getEvents().getId())

        // 좌석 세팅 + 상태 전이
        var seatRef = eventSeatRepository.getReferenceById(eventSeatId);
        r.selectEventSeat(seatRef); // 아래 엔티티 메서드 추가

        return ReservationResponseDto.from(r);
    }

}
