package com.example.demo.repository;

import com.example.demo.entity.Chapter;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    Optional<Chapter> findByNumber(Integer number);
}
