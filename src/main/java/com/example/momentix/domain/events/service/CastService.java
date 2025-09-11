package com.example.momentix.domain.events.service;

import com.example.momentix.domain.events.dto.request.CreateCastRequestDto;
import com.example.momentix.domain.events.entity.casts.Casts;
import com.example.momentix.domain.events.repository.casts.CastsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CastService {
    private final CastsRepository castsRepository;

    @Transactional
    public Casts createCast(CreateCastRequestDto request) {
        Casts castResponse = castsRepository.findByCastName(request.getCastName()).orElseGet(
                () -> new Casts(
                        request.getCastName(),
                        request.getCastImage()
                ));
        castsRepository.save(castResponse);
        return castResponse;
    }

    @Transactional
    public List<Casts> createCast(List<CreateCastRequestDto> request) {
        Set<String> requestCastNameSet = request.stream()
                .map(CreateCastRequestDto::getCastName)
                .collect(Collectors.toSet());

        Set<String> existCastNameSet = castsRepository.findAllByCastNameIn(requestCastNameSet).stream()
                .map(Casts::getCastName)
                .collect(Collectors.toSet());

        requestCastNameSet.removeAll(existCastNameSet);

        List<Casts> newCastList = request.stream()
                .filter(requestDto -> requestCastNameSet.contains(requestDto.getCastName()))
                .map(requestDto -> new Casts(
                        requestDto.getCastName(),
                        requestDto.getCastImage()))
                .toList();

        castsRepository.saveAll(newCastList);
        return newCastList;
    }
}
