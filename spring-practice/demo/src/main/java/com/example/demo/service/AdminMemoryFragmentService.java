package com.example.demo.service;

import com.example.demo.dto.MemoryFragmentForm;
import com.example.demo.entity.MemoryFragment;
import com.example.demo.repository.MemoryFragmentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminMemoryFragmentService {

    private final MemoryFragmentRepository memoryFragmentRepository;

    @Transactional(readOnly = true)
    public List<MemoryFragment> findAll() {
        return memoryFragmentRepository.findAll();
    }

    public MemoryFragmentForm newForm() {
        return new MemoryFragmentForm();
    }

    @Transactional(readOnly = true)
    public MemoryFragmentForm editForm(Long id) {
        MemoryFragment fragment = memoryFragmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("記憶断片が見つかりません: " + id));
        MemoryFragmentForm form = new MemoryFragmentForm();
        form.setId(fragment.getId());
        form.setTag(fragment.getTag());
        form.setTitle(fragment.getTitle());
        return form;
    }

    @Transactional
    public MemoryFragment save(MemoryFragmentForm form) {
        MemoryFragment fragment = form.getId() != null
                ? memoryFragmentRepository.findById(form.getId())
                        .orElseThrow(() -> new EntityNotFoundException("記憶断片が見つかりません: " + form.getId()))
                : new MemoryFragment();
        fragment.setTag(form.getTag());
        fragment.setTitle(form.getTitle());
        return memoryFragmentRepository.save(fragment);
    }

    @Transactional
    public void delete(Long id) {
        memoryFragmentRepository.deleteById(id);
    }
}
