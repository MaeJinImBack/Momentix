package com.example.momentix.domain.bookmark.repository;

import com.example.momentix.domain.bookmark.entity.Bookmark;
import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // User와 Event로 기존 즐겨찾기 정보가 있는지 조회
    Optional<Bookmark> findByUsersAndEvents(Users user, Events event);

    Page<Bookmark> findByUsersAndBookmarkStatusTrue(Users user, Pageable pageable);

}