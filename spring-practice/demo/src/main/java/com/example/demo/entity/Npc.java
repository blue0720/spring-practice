package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "npc")
@Getter
@Setter
@NoArgsConstructor
public class Npc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // NPCタイプ(例: 孤独な貧困層、ギルド幹部・権力者)。相性表はEmotionTagのweakTag/hateTagで表現する。
    private String role;

    @Column(name = "portrait_label")
    private String portraitLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "weak_tag", length = 20)
    private EmotionTag weakTag;

    @Enumerated(EnumType.STRING)
    @Column(name = "hate_tag", length = 20)
    private EmotionTag hateTag;

    @Column(name = "intro_text", columnDefinition = "TEXT")
    private String introText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @OneToMany(mappedBy = "npc")
    private List<NpcReactionLine> reactionLines = new ArrayList<>();
}
