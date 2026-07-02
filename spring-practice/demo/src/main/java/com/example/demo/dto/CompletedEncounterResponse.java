package com.example.demo.dto;

import com.example.demo.entity.EncounterOutcome;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompletedEncounterResponse {

    private String npcName;
    private EncounterOutcome outcome;
    private Integer gauge;
    private LocalDateTime playedAt;
}
