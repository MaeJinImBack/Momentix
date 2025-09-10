package com.example.momentix.domain.events.controller;

import com.example.momentix.domain.events.entity.Events;
import com.example.momentix.domain.events.entity.eventimages.EventImage;
import com.example.momentix.domain.events.repository.EventsRepository;
import com.example.momentix.domain.events.repository.eventimages.EventImagesRepository;
import com.example.momentix.domain.events.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/images")
public class EventImageController {

    private final S3UploadService s3UploadService;
    private final EventsRepository eventsRepository;
    private final EventImagesRepository eventImagesRepository;

    @PostMapping("/{imageType}")
    @Transactional
    public ResponseEntity<String> uploadImage(
            @PathVariable Long eventId,
            @PathVariable String imageType,
            @RequestParam("imageFile") MultipartFile file) throws IOException {

        String imageUrl = s3UploadService.upload(file, imageType);

        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공연을 찾을 수 없습니다."));

        EventImage eventImage = eventImagesRepository.findByEvents(event)
                .orElse(new EventImage(event));

        if ("poster".equalsIgnoreCase(imageType)) {
            eventImage.updatePosterImageUrl(imageUrl);
        } else if ("detail".equalsIgnoreCase(imageType)) {
            eventImage.updateDetailImageUrl(imageUrl);
        } else {
            throw new IllegalArgumentException("유효하지 않은 이미지 타입입니다.");
        }
        eventImagesRepository.save(eventImage);

        return ResponseEntity.ok("업로드 성공: " + imageUrl);
    }
}