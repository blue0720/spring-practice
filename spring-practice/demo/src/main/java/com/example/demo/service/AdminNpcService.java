package com.example.demo.service;

import com.example.demo.dto.NpcForm;
import com.example.demo.dto.NpcReactionLineForm;
import com.example.demo.dto.NpcRow;
import com.example.demo.entity.Chapter;
import com.example.demo.entity.Npc;
import com.example.demo.entity.NpcReactionLine;
import com.example.demo.entity.ReactionType;
import com.example.demo.repository.ChapterRepository;
import com.example.demo.repository.NpcReactionLineRepository;
import com.example.demo.repository.NpcRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminNpcService {

    // 反応テキストを新規に追加できるよう、フォーム表示時に付け足す空行の数。
    private static final int BLANK_REACTION_ROWS = 3;

    private final NpcRepository npcRepository;
    private final NpcReactionLineRepository npcReactionLineRepository;
    private final ChapterRepository chapterRepository;

    @Transactional(readOnly = true)
    public List<NpcRow> findAll() {
        return npcRepository.findAll().stream()
                .map(npc -> new NpcRow(
                        npc.getId(),
                        npc.getName(),
                        npc.getRole(),
                        npc.getWeakTag(),
                        npc.getHateTag(),
                        npc.getChapter() != null ? npc.getChapter().getTitle() : null))
                .toList();
    }

    public NpcForm newForm() {
        NpcForm form = new NpcForm();
        addBlankReactionRows(form);
        return form;
    }

    @Transactional(readOnly = true)
    public NpcForm editForm(Long id) {
        Npc npc = npcRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NPCが見つかりません: " + id));

        NpcForm form = new NpcForm();
        form.setId(npc.getId());
        form.setName(npc.getName());
        form.setRole(npc.getRole());
        form.setPortraitLabel(npc.getPortraitLabel());
        form.setWeakTag(npc.getWeakTag());
        form.setHateTag(npc.getHateTag());
        form.setIntroText(npc.getIntroText());
        form.setChapterId(npc.getChapter() != null ? npc.getChapter().getId() : null);

        List<NpcReactionLineForm> lines = new ArrayList<>();
        for (NpcReactionLine line : npcReactionLineRepository.findByNpcId(id)) {
            NpcReactionLineForm lineForm = new NpcReactionLineForm();
            lineForm.setType(line.getType());
            lineForm.setText(line.getText());
            lines.add(lineForm);
        }
        form.setReactionLines(lines);
        addBlankReactionRows(form);
        return form;
    }

    @Transactional
    public Npc save(NpcForm form) {
        Npc npc = form.getId() != null
                ? npcRepository.findById(form.getId())
                        .orElseThrow(() -> new EntityNotFoundException("NPCが見つかりません: " + form.getId()))
                : new Npc();

        npc.setName(form.getName());
        npc.setRole(form.getRole());
        npc.setPortraitLabel(form.getPortraitLabel());
        npc.setWeakTag(form.getWeakTag());
        npc.setHateTag(form.getHateTag());
        npc.setIntroText(form.getIntroText());
        npc.setChapter(form.getChapterId() != null
                ? chapterRepository.findById(form.getChapterId())
                        .orElseThrow(() -> new EntityNotFoundException("章が見つかりません: " + form.getChapterId()))
                : null);

        Npc saved = npcRepository.save(npc);
        replaceReactionLines(saved, form.getReactionLines());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        npcReactionLineRepository.deleteAll(npcReactionLineRepository.findByNpcId(id));
        npcRepository.deleteById(id);
    }

    private void replaceReactionLines(Npc npc, List<NpcReactionLineForm> forms) {
        npcReactionLineRepository.deleteAll(npcReactionLineRepository.findByNpcId(npc.getId()));
        for (NpcReactionLineForm form : forms) {
            if (form.getText() == null || form.getText().isBlank()) {
                continue;
            }
            NpcReactionLine line = new NpcReactionLine();
            line.setNpc(npc);
            line.setType(form.getType() != null ? form.getType() : ReactionType.NEUTRAL);
            line.setText(form.getText());
            npcReactionLineRepository.save(line);
        }
    }

    private void addBlankReactionRows(NpcForm form) {
        for (int i = 0; i < BLANK_REACTION_ROWS; i++) {
            form.getReactionLines().add(new NpcReactionLineForm());
        }
    }
}
