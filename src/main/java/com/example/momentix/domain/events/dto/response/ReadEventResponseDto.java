package com.example.momentix.domain.events.dto.response;

import com.example.momentix.domain.events.entity.enums.AgeRatingType;
import com.example.momentix.domain.events.entity.enums.EventCategoryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReadEventResponseDto {
    private Long id;
    private String eventTitle;
    private EventCategoryType eventCategoryType;
    private AgeRatingType ageRatingType;
    private String placeName;
    private String placeAddress;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private List<EventTimeResponseDto> eventTimeResponseDtoList;
    private List<ReservationTimeResponseDto> reservationTimeResponseDtoList;
    private List<ReadCastResponseDto> readCastResponseDtoList;


}
