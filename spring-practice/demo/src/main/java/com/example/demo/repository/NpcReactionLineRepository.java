package com.example.demo.repository;

import com.example.demo.entity.NpcReactionLine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NpcReactionLineRepository extends JpaRepository<NpcReactionLine, Long> {

    List<NpcReactionLine> findByNpcId(Long npcId);
}
