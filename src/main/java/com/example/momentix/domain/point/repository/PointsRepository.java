package com.example.momentix.domain.point.repository;

import com.example.momentix.domain.point.entity.Points;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointsRepository extends JpaRepository <Points, Long>{
}
