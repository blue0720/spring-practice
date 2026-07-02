package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.demo.entity.Chapter;
import com.example.demo.repository.ChapterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin", roles = "ADMIN")
class AdminChapterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChapterRepository chapterRepository;

    @Test
    void createEditDeleteChapter() throws Exception {
        mockMvc.perform(post("/admin/chapters")
                        .with(csrf())
                        .param("number", "1")
                        .param("title", "第一章 灰塵市")
                        .param("description", "序章"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/chapters"));

        Chapter saved = chapterRepository.findByNumber(1).orElseThrow();
        assertEquals("第一章 灰塵市", saved.getTitle());

        mockMvc.perform(post("/admin/chapters/" + saved.getId())
                        .with(csrf())
                        .param("number", "1")
                        .param("title", "第一章 灰塵市(改)")
                        .param("description", "序章(改)"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/chapters"));

        Chapter updated = chapterRepository.findById(saved.getId()).orElseThrow();
        assertEquals("第一章 灰塵市(改)", updated.getTitle());

        mockMvc.perform(post("/admin/chapters/" + saved.getId() + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/chapters"));

        assertTrue(chapterRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    void createChapter_withoutTitle_rendersFormWithErrors() throws Exception {
        mockMvc.perform(post("/admin/chapters")
                        .with(csrf())
                        .param("number", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/chapters/form"))
                .andExpect(model().attributeHasFieldErrors("chapterForm", "title"));
    }
}
