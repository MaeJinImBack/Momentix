package com.example.momentix.domain.point.service;

import com.example.momentix.domain.point.repository.PointsRepository;
import org.springframework.stereotype.Service;

//비관적락+멱등키
@Service
public class PointService {

    private final PointsRepository pointRepository;

    public PointService(PointsRepository pointRepository){
        this.pointRepository=pointRepository;
    }
}
