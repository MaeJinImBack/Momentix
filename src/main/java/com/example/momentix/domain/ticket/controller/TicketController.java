package com.example.momentix.domain.ticket.controller;

import com.example.momentix.domain.ticket.dto.request.CreateTicketRequestDto;
import com.example.momentix.domain.ticket.dto.response.TicketResponseDto;
import com.example.momentix.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponseDto> createTicket(@RequestBody CreateTicketRequestDto requestDto) {
        TicketResponseDto response = ticketService.createTicket(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}