package com.example.demo.controller;

import com.example.demo.dto.ChapterForm;
import com.example.demo.service.AdminChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/chapters")
@RequiredArgsConstructor
public class AdminChapterController {

    private final AdminChapterService adminChapterService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("chapters", adminChapterService.findAll());
        return "admin/chapters/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("chapterForm", adminChapterService.newForm());
        return "admin/chapters/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("chapterForm", adminChapterService.editForm(id));
        return "admin/chapters/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("chapterForm") ChapterForm chapterForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/chapters/form";
        }
        adminChapterService.save(chapterForm);
        return "redirect:/admin/chapters";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("chapterForm") ChapterForm chapterForm,
            BindingResult bindingResult) {
        chapterForm.setId(id);
        if (bindingResult.hasErrors()) {
            return "admin/chapters/form";
        }
        adminChapterService.save(chapterForm);
        return "redirect:/admin/chapters";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminChapterService.delete(id);
        return "redirect:/admin/chapters";
    }
}
