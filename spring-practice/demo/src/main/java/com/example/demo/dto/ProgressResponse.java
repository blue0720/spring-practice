package com.example.demo.dto;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProgressResponse {

    private Integer currentChapter;
    private Set<String> clearedDistrictIds;
    private List<CompletedEncounterResponse> completedEncounters;
}
