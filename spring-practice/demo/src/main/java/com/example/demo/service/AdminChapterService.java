package com.example.demo.service;

import com.example.demo.dto.ChapterForm;
import com.example.demo.entity.Chapter;
import com.example.demo.repository.ChapterRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminChapterService {

    private final ChapterRepository chapterRepository;

    @Transactional(readOnly = true)
    public List<Chapter> findAll() {
        return chapterRepository.findAll();
    }

    public ChapterForm newForm() {
        return new ChapterForm();
    }

    @Transactional(readOnly = true)
    public ChapterForm editForm(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("章が見つかりません: " + id));
        ChapterForm form = new ChapterForm();
        form.setId(chapter.getId());
        form.setNumber(chapter.getNumber());
        form.setTitle(chapter.getTitle());
        form.setDescription(chapter.getDescription());
        return form;
    }

    @Transactional
    public Chapter save(ChapterForm form) {
        Chapter chapter = form.getId() != null
                ? chapterRepository.findById(form.getId())
                        .orElseThrow(() -> new EntityNotFoundException("章が見つかりません: " + form.getId()))
                : new Chapter();
        chapter.setNumber(form.getNumber());
        chapter.setTitle(form.getTitle());
        chapter.setDescription(form.getDescription());
        return chapterRepository.save(chapter);
    }

    @Transactional
    public void delete(Long id) {
        chapterRepository.deleteById(id);
    }
}
