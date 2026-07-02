package com.example.demo.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.entity.Chapter;
import com.example.demo.entity.EmotionTag;
import com.example.demo.entity.MemoryFragment;
import com.example.demo.entity.Npc;
import com.example.demo.repository.ChapterRepository;
import com.example.demo.repository.MemoryFragmentRepository;
import com.example.demo.repository.NpcReactionLineRepository;
import com.example.demo.repository.NpcRepository;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * {@link ContentSeeder} が投入した第1〜3章コンテンツを検証する。
 * とりわけ「全NPCの相性表(4NPCタイプ×8感情タグ)がゲームロジックと一致していること」
 * (チケット07の確認事項)を、game-data.js の AFFINITY_TABLE と同じ4組を突き合わせて保証する。
 */
@SpringBootTest
class ContentSeederTest {

    // static/js/game-data.js の AFFINITY_TABLE と対応させたNPC名→(weak, hate)の対応。
    private static final Map<String, EmotionTag[]> EXPECTED_AFFINITY = Map.of(
            "ノラ", new EmotionTag[] {EmotionTag.安心, EmotionTag.恐怖},
            "ヴァイス", new EmotionTag[] {EmotionTag.誇り, EmotionTag.恥},
            "レイム", new EmotionTag[] {EmotionTag.愛情, EmotionTag.怒り},
            "テオドール", new EmotionTag[] {EmotionTag.喜び, EmotionTag.悲しみ});

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private NpcRepository npcRepository;

    @Autowired
    private NpcReactionLineRepository npcReactionLineRepository;

    @Autowired
    private MemoryFragmentRepository memoryFragmentRepository;

    @Test
    void chapters1To3AreSeeded() {
        List<Integer> numbers = chapterRepository.findAll().stream().map(Chapter::getNumber).toList();
        assertTrue(numbers.containsAll(List.of(1, 2, 3)));
    }

    @Test
    void mainNpcAffinityMatchesGameLogicAffinityTable() {
        for (Map.Entry<String, EmotionTag[]> entry : EXPECTED_AFFINITY.entrySet()) {
            Npc npc = npcRepository.findByName(entry.getKey()).orElseThrow();
            assertEquals(entry.getValue()[0], npc.getWeakTag(), entry.getKey() + " の弱点タグ");
            assertEquals(entry.getValue()[1], npc.getHateTag(), entry.getKey() + " の苦手タグ");
            assertTrue(npcReactionLineRepository.findByNpcId(npc.getId()).size() >= 2,
                    entry.getKey() + " には反応テキストが複数登録されているはず");
        }
    }

    @Test
    void seleneIsIntentionallyWithoutAffinityTag() {
        Npc selene = npcRepository.findByName("セレーネ").orElseThrow();
        assertNull(selene.getWeakTag());
        assertNull(selene.getHateTag());
    }

    @Test
    void mobNpcHasNoNegotiationReactionLines() {
        Npc elder = npcRepository.findByName("常連の老人").orElseThrow();
        assertTrue(npcReactionLineRepository.findByNpcId(elder.getId()).isEmpty());
    }

    @Test
    void memoryFragmentPoolCoversAllEightEmotionTags() {
        Set<EmotionTag> tags = memoryFragmentRepository.findAll().stream()
                .map(MemoryFragment::getTag)
                .collect(Collectors.toSet());
        assertEquals(EnumSet.allOf(EmotionTag.class), tags);
    }
}
