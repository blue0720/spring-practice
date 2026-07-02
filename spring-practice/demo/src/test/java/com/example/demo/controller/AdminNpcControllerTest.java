package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.entity.Npc;
import com.example.demo.entity.NpcReactionLine;
import com.example.demo.repository.NpcReactionLineRepository;
import com.example.demo.repository.NpcRepository;
import java.util.List;
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
class AdminNpcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NpcRepository npcRepository;

    @Autowired
    private NpcReactionLineRepository npcReactionLineRepository;

    @Test
    void createNpc_savesOnlyNonBlankReactionLines() throws Exception {
        mockMvc.perform(post("/admin/npcs")
                        .with(csrf())
                        .param("name", "ノラ")
                        .param("weakTag", "安心")
                        .param("hateTag", "恐怖")
                        .param("reactionLines[0].type", "GOOD")
                        .param("reactionLines[0].text", "ありがとう")
                        .param("reactionLines[1].type", "BAD")
                        .param("reactionLines[1].text", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/npcs"));

        Npc saved = npcRepository.findByName("ノラ").orElseThrow();
        List<NpcReactionLine> lines = npcReactionLineRepository.findByNpcId(saved.getId());
        assertEquals(1, lines.size());
        assertEquals("ありがとう", lines.get(0).getText());

        mockMvc.perform(get("/admin/npcs/" + saved.getId() + "/edit"))
                .andExpect(status().isOk());
    }

    @Test
    void createNpc_withoutRequiredTags_rendersFormWithErrors() throws Exception {
        mockMvc.perform(post("/admin/npcs")
                        .with(csrf())
                        .param("name", "名無し"))
                .andExpect(status().isOk());
    }
}
