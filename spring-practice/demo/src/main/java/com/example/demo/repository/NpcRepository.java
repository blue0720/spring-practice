package com.example.demo.repository;

import com.example.demo.entity.Npc;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NpcRepository extends JpaRepository<Npc, Long> {

    List<Npc> findByChapterId(Long chapterId);

    Optional<Npc> findByName(String name);
}
