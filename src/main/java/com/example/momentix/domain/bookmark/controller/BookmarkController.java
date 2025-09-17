package com.example.momentix.domain.bookmark.controller;

import com.example.momentix.domain.auth.impl.UserDetailsImpl;
import com.example.momentix.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/users/favorites/events/{eventId}")
    public ResponseEntity<?> toggleBookmark(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        boolean resultStatus = bookmarkService.toggleBookmark(eventId, userDetails.getUser());

        // 응답 본문을 Map을 사용해 동적으로 생성
        Map<String, Boolean> response = Map.of("bookmarkStatus", resultStatus);

        return new ResponseEntity<>(response, HttpStatus.OK); // 201 Created 대신 200 OK가 더 적합
    }
}