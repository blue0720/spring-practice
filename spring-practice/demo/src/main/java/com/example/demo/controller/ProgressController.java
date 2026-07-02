package com.example.demo.controller;

import com.example.demo.dto.ProgressResponse;
import com.example.demo.dto.SaveProgressRequest;
import com.example.demo.service.ProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping
    public ProgressResponse getProgress(Principal principal) {
        return progressService.getProgress(principal.getName());
    }

    @PostMapping
    public ProgressResponse saveProgress(Principal principal, @Valid @RequestBody SaveProgressRequest request) {
        return progressService.saveProgress(principal.getName(), request);
    }
}
