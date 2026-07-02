package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveProgressRequest {

    @NotNull
    @Min(1)
    private Integer currentChapter;

    private Set<String> clearedDistrictIds = new HashSet<>();

    @Valid
    private EncounterRequest lastEncounter;
}
