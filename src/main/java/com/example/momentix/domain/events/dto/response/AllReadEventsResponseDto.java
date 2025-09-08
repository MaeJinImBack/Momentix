package com.example.momentix.domain.events.dto.response;

import com.example.momentix.domain.events.entity.enums.EventCategoryType;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.events.entity.places.Places;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class AllReadEventsResponseDto {
       private String eventTitle;
       private String eventCategory;
       private String placeName;
       private LocalDate eventStartDate;
       private LocalDate eventEndDate;

       public AllReadEventsResponseDto(String eventTitle, EventCategoryType eventCategory, LocalDate eventStartDate, LocalDate eventEndDate, String placeName) {
              this.eventTitle = eventTitle;
              this.eventCategory = eventCategory.name();
              this.eventStartDate = eventStartDate;
              this.eventEndDate = eventEndDate;
              this.placeName = placeName;

       }

}
