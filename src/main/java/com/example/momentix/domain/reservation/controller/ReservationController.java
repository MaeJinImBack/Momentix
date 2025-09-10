package com.example.momentix.domain.reservation.controller;



import com.example.momentix.domain.auth.impl.UserDetailsImpl;
import com.example.momentix.domain.reservation.dto.ReservationResponseDto;
import com.example.momentix.domain.reservation.entitiy.Reservations;
import com.example.momentix.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
