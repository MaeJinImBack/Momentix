package com.example.momentix.domain.reservation.controller;


import com.example.momentix.domain.auth.impl.UserDetailsImpl;
import com.example.momentix.domain.reservation.dto.ReservationResponseDto;
import com.example.momentix.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 공연, 장소, 시간 선택
    @PostMapping("/events/{eventId}/{eventPlaceId}/{eventTimeId}")
    public ResponseEntity<ReservationResponseDto> selectAllReservations(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long eventId,
            @PathVariable Long eventPlaceId,
            @PathVariable Long eventTimeId){
        ReservationResponseDto reservations = reservationService.selectAll(userDetails.getUserId(), eventId, eventPlaceId, eventTimeId);

        return  new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    //공연 선택
    @PostMapping("/events/{eventId}")
    public ResponseEntity<ReservationResponseDto> selectEvent(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable Long eventId) {

        ReservationResponseDto reservations = reservationService.selectEvent(userDetails.getUserId(), eventId);

        return new ResponseEntity<>(reservations, HttpStatus.CREATED);
    }


    //공연 장소 선택
    @PostMapping("/{reservationId}/select-event-place/{eventPlaceId}")
    public ResponseEntity<ReservationResponseDto> selectEventPlace(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   @PathVariable Long reservationId,
                                                                   @PathVariable Long eventPlaceId) {
        ReservationResponseDto reservationResponseDto
                = reservationService.selectEventPlace(userDetails.getUserId(), reservationId, eventPlaceId);

        return new ResponseEntity<>(reservationResponseDto, HttpStatus.OK);
    }


    //공연 시간 선택
    @PostMapping("/{reservationId}/select-event-time/{eventTimeId}")
    public ResponseEntity<ReservationResponseDto> selectEventTime(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                  @PathVariable Long reservationId,
                                                                  @PathVariable Long eventTimeId) {

        ReservationResponseDto reservationResponseDto
                = reservationService.selectEventTime(userDetails.getUserId(), reservationId, eventTimeId);

        return new ResponseEntity<>(reservationResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @PathVariable Long reservationId) {


        reservationService.deleteReservation(userDetails.getUserId(), reservationId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    // 좌석 선택
    @PostMapping("/{reservationId}/seat/{eventTimeReserveSeatId}")
    public ResponseEntity<ReservationResponseDto> selectSeat(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("reservationId") Long reservationId,
            @PathVariable("eventTimeReserveSeatId") Long eventTimeReserveSeatId
    ) {
        ReservationResponseDto dto =
                reservationService.selectEventSeat(userDetails.getUserId(), reservationId, eventTimeReserveSeatId);
        return ResponseEntity.ok(dto);
    }

}
