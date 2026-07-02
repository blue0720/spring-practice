package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterForm {

    private Long id;

    @NotNull
    @Min(1)
    private Integer number;

    @NotBlank
    private String title;

    private String description;
}
