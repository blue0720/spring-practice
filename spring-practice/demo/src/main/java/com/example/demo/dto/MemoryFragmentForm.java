package com.example.demo.dto;

import com.example.demo.entity.EmotionTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoryFragmentForm {

    private Long id;

    @NotNull
    private EmotionTag tag;

    @NotBlank
    private String title;
}
