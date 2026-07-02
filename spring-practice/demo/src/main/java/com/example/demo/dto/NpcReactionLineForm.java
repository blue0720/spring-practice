package com.example.demo.dto;

import com.example.demo.entity.ReactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NpcReactionLineForm {

    private ReactionType type;

    private String text;
}
