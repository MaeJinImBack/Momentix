package com.example.momentix.domain.events.service;

import com.example.momentix.domain.events.dto.request.PlacesRequestDto;
import com.example.momentix.domain.events.dto.request.SeatRequestDto;
import com.example.momentix.domain.events.entity.EventSeat;
import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.places.Places;
import com.example.momentix.domain.events.entity.seats.Seats;
import com.example.momentix.domain.events.repository.EventPlaceRepository;
import com.example.momentix.domain.events.repository.EventsRepository;
import com.example.momentix.domain.events.repository.places.PlacesRepository;
import com.example.momentix.domain.events.repository.seats.SeatsRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final EventsRepository eventsRepository;
    private final PlacesRepository placesRepository;
    private final EventPlaceRepository eventPlaceRepository;
    private final SeatsRepository seatsRepository;

    public List<SeatRequestDto> createSeat(MultipartFile seatFile,
                                           PlacesRequestDto placeRequest,
                                           Long eventId) {
        // 좌석 등급 설정할 공연이 맞는지 확인
        Events events = eventsRepository.findById(eventId).orElseThrow(() -> new IllegalIdentifierException("event 없음"));
        // 좌석 등급 설정할 공연장 확인
        Places places = placesRepository.findByPlaceName(placeRequest.getPlaceName()).orElseThrow(() -> new IllegalIdentifierException("공연장 없음"));
        // 공연과 공연장 일치 확인
        if (!eventPlaceRepository.existsByEventsAndPlaces(events, places)) {throw new IllegalIdentifierException("공연과 공연장이 일치하지 않음");}
        // File을 문자열 형태로 받기
        try (Reader reader = new InputStreamReader(seatFile.getInputStream())) {
            // csv 파일을 List <Dto> 형태로 반환
            List<SeatRequestDto> seatList = new CsvToBeanBuilder<SeatRequestDto>(reader)
                    .withType(SeatRequestDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            // 받은 좌석 리스트를 DB에 저장  // 성능 개선 필요
            for (SeatRequestDto seatRequest : seatList) {
                Seats addSeat = Seats.builder()
                        .seatRow(seatRequest.getSeatRow())
                        .seatCol(seatRequest.getSeatCol())
                        .build();
                places.addSeats(addSeat);
                seatsRepository.save(addSeat);

                events.addEventSeat(EventSeat.builder()
                        .seatGradeType(seatRequest.getSeatGradeType())
                        .seatPartType(seatRequest.getSeatPartType())
                        .seatPrice(seatRequest.getSeatPrice())
                        .seat(addSeat)
                        .build());
            }
            eventsRepository.save(events);
            placesRepository.save(places);

            return seatList;
        } catch (IOException e) {
            return null;
        }

    }
}
