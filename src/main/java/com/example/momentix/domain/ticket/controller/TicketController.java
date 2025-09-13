package com.example.momentix.domain.ticket.controller;

import com.example.momentix.domain.auth.impl.UserDetailsImpl;
import com.example.momentix.domain.ticket.dto.request.CreateTicketRequestDto;
import com.example.momentix.domain.ticket.dto.request.UpdateTicketStatusRequestDto;
import com.example.momentix.domain.ticket.dto.response.TicketResponseDto;
import com.example.momentix.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponseDto> createTicket(@RequestBody CreateTicketRequestDto requestDto) {
        TicketResponseDto response = ticketService.createTicket(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("/tickets")
    public ResponseEntity<Page<TicketResponseDto>> getMyTickets(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TicketResponseDto> response = ticketService.getMyTickets(userDetails.getUser(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<TicketResponseDto> getTicket(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        TicketResponseDto response = ticketService.getTicket(ticketId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/tickets/{ticketId}")
    public ResponseEntity<String> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestBody UpdateTicketStatusRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ticketService.updateTicketStatus(ticketId, requestDto, userDetails.getUser());
        return ResponseEntity.ok("예매 상태가 성공적으로 변경되었습니다.");
    }

    @DeleteMapping("/tickets/{ticketId}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ticketService.softDeleteTicketByAdmin(ticketId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

}