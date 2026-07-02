package com.example.demo.dto;

import com.example.demo.entity.EmotionTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NpcForm {

    private Long id;

    @NotBlank
    private String name;

    private String role;

    private String portraitLabel;

    @NotNull
    private EmotionTag weakTag;

    @NotNull
    private EmotionTag hateTag;

    private String introText;

    private Long chapterId;

    // 反応テキスト行。空欄(text未入力)の行は保存時に無視される。
    private List<NpcReactionLineForm> reactionLines = new ArrayList<>();
}
