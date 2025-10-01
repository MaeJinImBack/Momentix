package com.example.momentix.domain.test;

import com.example.momentix.domain.reservation.dto.ReservationResponseDto;
import com.example.momentix.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final ReservationService reservationService;

    // 좌석 선택
    @PostMapping("/seat/{eventTimeReserveSeatId}")
    public ResponseEntity<ReservationResponseDto> selectSeat(
            @PathVariable("eventTimeReserveSeatId") Long eventTimeReserveSeatId
    ) {
        ReservationResponseDto dto =
                reservationService.testSelectEventSeat(eventTimeReserveSeatId);

        return ResponseEntity.ok(dto);
    }
}
