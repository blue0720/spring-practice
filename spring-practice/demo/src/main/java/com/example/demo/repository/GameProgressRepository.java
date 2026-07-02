package com.example.demo.repository;

import com.example.demo.entity.GameProgress;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameProgressRepository extends JpaRepository<GameProgress, Long> {

    Optional<GameProgress> findByUserId(Long userId);
}
