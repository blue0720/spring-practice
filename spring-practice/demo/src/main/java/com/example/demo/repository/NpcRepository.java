package com.example.demo.repository;

import com.example.demo.entity.Npc;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NpcRepository extends JpaRepository<Npc, Long> {

    List<Npc> findByChapterId(Long chapterId);
}
