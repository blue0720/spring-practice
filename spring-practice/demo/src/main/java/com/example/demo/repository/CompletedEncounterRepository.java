package com.example.demo.repository;

import com.example.demo.entity.CompletedEncounter;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedEncounterRepository extends JpaRepository<CompletedEncounter, Long> {

    List<CompletedEncounter> findByGameProgressId(Long gameProgressId);

    List<CompletedEncounter> findByGameProgressIdOrderByPlayedAtDesc(Long gameProgressId);
}
