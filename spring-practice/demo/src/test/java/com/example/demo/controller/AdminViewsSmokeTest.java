package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class AdminViewsSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void allListViewsRender() throws Exception {
        mockMvc.perform(get("/admin/npcs")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/memories")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/districts")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/chapters")).andExpect(status().isOk());
    }

    @Test
    void allNewFormViewsRender() throws Exception {
        mockMvc.perform(get("/admin/npcs/new")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/memories/new")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/districts/new")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/chapters/new")).andExpect(status().isOk());
    }
}
