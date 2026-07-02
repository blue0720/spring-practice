package com.example.demo.service;

import com.example.demo.dto.CompletedEncounterResponse;
import com.example.demo.dto.EncounterRequest;
import com.example.demo.dto.ProgressResponse;
import com.example.demo.dto.SaveProgressRequest;
import com.example.demo.entity.CompletedEncounter;
import com.example.demo.entity.GameProgress;
import com.example.demo.entity.Npc;
import com.example.demo.entity.User;
import com.example.demo.repository.CompletedEncounterRepository;
import com.example.demo.repository.GameProgressRepository;
import com.example.demo.repository.NpcRepository;
import com.example.demo.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final UserRepository userRepository;
    private final GameProgressRepository gameProgressRepository;
    private final CompletedEncounterRepository completedEncounterRepository;
    private final NpcRepository npcRepository;

    @Transactional
    public ProgressResponse getProgress(String username) {
        GameProgress progress = findOrCreateProgress(username);
        return toResponse(progress);
    }

    @Transactional
    public ProgressResponse saveProgress(String username, SaveProgressRequest request) {
        GameProgress progress = findOrCreateProgress(username);
        progress.setCurrentChapter(request.getCurrentChapter());
        progress.getClearedDistrictIds().addAll(request.getClearedDistrictIds());

        EncounterRequest lastEncounter = request.getLastEncounter();
        if (lastEncounter != null) {
            CompletedEncounter encounter = new CompletedEncounter();
            encounter.setGameProgress(progress);
            encounter.setOutcome(lastEncounter.getOutcome());
            encounter.setGauge(lastEncounter.getGauge());
            npcRepository.findByName(lastEncounter.getNpcName()).ifPresent(encounter::setNpc);
            completedEncounterRepository.save(encounter);
        }

        gameProgressRepository.save(progress);
        return toResponse(progress);
    }

    private GameProgress findOrCreateProgress(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

        return gameProgressRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    GameProgress progress = new GameProgress();
                    progress.setUser(user);
                    return gameProgressRepository.save(progress);
                });
    }

    private ProgressResponse toResponse(GameProgress progress) {
        List<CompletedEncounterResponse> encounters = completedEncounterRepository
                .findByGameProgressIdOrderByPlayedAtDesc(progress.getId()).stream()
                .map(e -> new CompletedEncounterResponse(
                        e.getNpc() != null ? e.getNpc().getName() : null,
                        e.getOutcome(),
                        e.getGauge(),
                        e.getPlayedAt()))
                .toList();

        return new ProgressResponse(progress.getCurrentChapter(), new HashSet<>(progress.getClearedDistrictIds()), encounters);
    }
}
