package com.example.momentix.domain.bookmark.service;

import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.repository.EventsRepository;
import com.example.momentix.domain.bookmark.entity.Bookmark;
import com.example.momentix.domain.bookmark.repository.BookmarkRepository;
import com.example.momentix.domain.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final EventsRepository eventsRepository;

    @Transactional
    public boolean toggleBookmark(Long eventId, Users user) {
        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공연을 찾을 수 없습니다."));

        // 1. 기존에 즐겨찾기 정보가 있는지 조회
        Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByUsersAndEvents(user, event);

        if (bookmarkOptional.isPresent()) {
            // 2. 정보가 있으면, 상태를 반전(toggle)시킵니다.
            Bookmark bookmark = bookmarkOptional.get();
            bookmark.toggleStatus();
            return bookmark.isBookmarkStatus();
        } else {
            // 3. 정보가 없으면, 새로 생성하고 저장합니다.
            Bookmark bookmark = new Bookmark(user, event);
            bookmarkRepository.save(bookmark);
            return true;
        }
    }
}