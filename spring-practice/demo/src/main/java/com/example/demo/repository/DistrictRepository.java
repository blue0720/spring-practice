package com.example.demo.repository;

import com.example.demo.entity.District;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<District, Long> {

    List<District> findByChapterId(Long chapterId);
}
