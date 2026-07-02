package com.example.demo.dto;

import com.example.demo.entity.EncounterOutcome;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncounterRequest {

    @NotBlank
    private String districtId;

    @NotBlank
    private String npcName;

    @NotNull
    private EncounterOutcome outcome;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer gauge;
}
