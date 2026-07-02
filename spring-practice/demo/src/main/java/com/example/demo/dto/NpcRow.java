package com.example.demo.dto;

import com.example.demo.entity.EmotionTag;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NpcRow {

    private Long id;
    private String name;
    private String role;
    private EmotionTag weakTag;
    private EmotionTag hateTag;
    private String chapterTitle;
}
