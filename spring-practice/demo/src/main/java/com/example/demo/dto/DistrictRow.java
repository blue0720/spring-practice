package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DistrictRow {

    private Long id;
    private String name;
    private String role;
    private String chapterTitle;
    private String npcName;
}
