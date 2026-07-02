package com.example.demo.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProgressControllerTest {

    private static final String USERNAME = "progress-test-user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setEmail(USERNAME + "@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.ROLE_PLAYER);
        userRepository.save(user);
    }

    @Test
    void getProgress_withoutAuthentication_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/api/progress"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void getProgress_returnsDefaultStateForNewUser() throws Exception {
        mockMvc.perform(get("/api/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentChapter").value(1))
                .andExpect(jsonPath("$.clearedDistrictIds").isEmpty())
                .andExpect(jsonPath("$.completedEncounters").isEmpty());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void postProgress_mergesClearedDistrictsAndRecordsEncounter() throws Exception {
        String body = """
                {
                  "currentChapter": 1,
                  "clearedDistrictIds": ["slum"],
                  "lastEncounter": {
                    "districtId": "slum",
                    "npcName": "ノラ",
                    "outcome": "SUCCESS",
                    "gauge": 85
                  }
                }
                """;

        mockMvc.perform(post("/api/progress")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clearedDistrictIds[0]").value("slum"))
                .andExpect(jsonPath("$.completedEncounters[0].outcome").value("SUCCESS"))
                .andExpect(jsonPath("$.completedEncounters[0].gauge").value(85));

        mockMvc.perform(get("/api/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clearedDistrictIds[0]").value("slum"))
                .andExpect(jsonPath("$.completedEncounters.length()").value(1));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void postProgress_withoutCurrentChapter_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/progress")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clearedDistrictIds\":[\"slum\"]}"))
                .andExpect(status().isBadRequest());
    }
}
