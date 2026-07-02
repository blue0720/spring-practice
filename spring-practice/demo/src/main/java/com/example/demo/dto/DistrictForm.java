package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistrictForm {

    private Long id;

    @NotBlank
    private String name;

    private String role;

    private Long chapterId;

    private Long npcId;
}
