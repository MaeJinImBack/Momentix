package com.example.momentix.domain.bookmark.dto.response;

import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.eventimages.EventImages;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookmarkResponseDto {
    private Long eventId;
    private String title;
    private String posterImageUrl;
    private LocalDate startDate;
    private LocalDate endDate;

    public BookmarkResponseDto(Events event, EventImages eventImages) {
        this.eventId = event.getId();
        this.title = event.getEventTitle();
        // eventImage가 null이 아닐 때만 URL을 설정
        if (eventImages != null) {
            this.posterImageUrl = eventImages.getPosterImageUrl();
        }
        this.startDate = event.getEventStartDate();
        this.endDate = event.getEventEndDate();
    }
}