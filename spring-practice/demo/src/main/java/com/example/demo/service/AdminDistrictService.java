package com.example.demo.service;

import com.example.demo.dto.DistrictForm;
import com.example.demo.dto.DistrictRow;
import com.example.demo.entity.District;
import com.example.demo.repository.ChapterRepository;
import com.example.demo.repository.DistrictRepository;
import com.example.demo.repository.NpcRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDistrictService {

    private final DistrictRepository districtRepository;
    private final ChapterRepository chapterRepository;
    private final NpcRepository npcRepository;

    @Transactional(readOnly = true)
    public List<DistrictRow> findAll() {
        return districtRepository.findAll().stream()
                .map(district -> new DistrictRow(
                        district.getId(),
                        district.getName(),
                        district.getRole(),
                        district.getChapter() != null ? district.getChapter().getTitle() : null,
                        district.getNpc() != null ? district.getNpc().getName() : null))
                .toList();
    }

    public DistrictForm newForm() {
        return new DistrictForm();
    }

    @Transactional(readOnly = true)
    public DistrictForm editForm(Long id) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("区画が見つかりません: " + id));
        DistrictForm form = new DistrictForm();
        form.setId(district.getId());
        form.setName(district.getName());
        form.setRole(district.getRole());
        form.setChapterId(district.getChapter() != null ? district.getChapter().getId() : null);
        form.setNpcId(district.getNpc() != null ? district.getNpc().getId() : null);
        return form;
    }

    @Transactional
    public District save(DistrictForm form) {
        District district = form.getId() != null
                ? districtRepository.findById(form.getId())
                        .orElseThrow(() -> new EntityNotFoundException("区画が見つかりません: " + form.getId()))
                : new District();
        district.setName(form.getName());
        district.setRole(form.getRole());
        district.setChapter(form.getChapterId() != null
                ? chapterRepository.findById(form.getChapterId())
                        .orElseThrow(() -> new EntityNotFoundException("章が見つかりません: " + form.getChapterId()))
                : null);
        district.setNpc(form.getNpcId() != null
                ? npcRepository.findById(form.getNpcId())
                        .orElseThrow(() -> new EntityNotFoundException("NPCが見つかりません: " + form.getNpcId()))
                : null);
        return districtRepository.save(district);
    }

    @Transactional
    public void delete(Long id) {
        districtRepository.deleteById(id);
    }
}
