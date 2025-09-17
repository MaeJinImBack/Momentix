package com.example.momentix.domain.bookmark.dto.response;

import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.eventimages.EventImage;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookmarkResponseDto {
    private Long eventId;
    private String title;
    private String posterImageUrl;
    private LocalDate startDate;
    private LocalDate endDate;

    public BookmarkResponseDto(Events event, EventImage eventImage) {
        this.eventId = event.getId();
        this.title = event.getEventTitle();
        // eventImage가 null이 아닐 때만 URL을 설정
        if (eventImage != null) {
            this.posterImageUrl = eventImage.getPosterImageUrl();
        }
        this.startDate = event.getEventStartDate();
        this.endDate = event.getEventEndDate();
    }
}