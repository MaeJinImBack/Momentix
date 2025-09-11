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

    @PostMapping("/events/{eventId}")
    public ResponseEntity<ReservationResponseDto> selectEvent(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @PathVariable Long eventId) {

        ReservationResponseDto reservations = reservationService.selectEvent(userDetails.getUserId(), eventId);

        return new ResponseEntity<>(reservations, HttpStatus.CREATED);
    }


    @PostMapping("/{reservationId}/select-event-place/{eventPlaceId}")
    public ResponseEntity<ReservationResponseDto> selectEventPlace(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   @PathVariable Long reservationId,
                                                                   @PathVariable Long eventPlaceId){
        ReservationResponseDto reservationResponseDto
               = reservationService.selectEventPlace(userDetails.getUserId(), reservationId, eventPlaceId);

        return new ResponseEntity<>(reservationResponseDto, HttpStatus.OK);
    }


    @PostMapping("/{reservationId}/select-event-time/{eventTimeId}")
    public ResponseEntity<ReservationResponseDto> selectEventTime(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                  @PathVariable Long reservationId,
                                                                  @PathVariable Long eventTimeId){

        ReservationResponseDto reservationResponseDto
                = reservationService.selectEventTime(userDetails.getUserId(), reservationId, eventTimeId);

        return new ResponseEntity<>(reservationResponseDto, HttpStatus.OK);

    }
}
