package com.example.demo.controller;

import com.example.demo.dto.DistrictForm;
import com.example.demo.repository.ChapterRepository;
import com.example.demo.repository.NpcRepository;
import com.example.demo.service.AdminDistrictService;
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
@RequestMapping("/admin/districts")
@RequiredArgsConstructor
public class AdminDistrictController {

    private final AdminDistrictService adminDistrictService;
    private final ChapterRepository chapterRepository;
    private final NpcRepository npcRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("districts", adminDistrictService.findAll());
        return "admin/districts/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("districtForm", adminDistrictService.newForm());
        addReferenceData(model);
        return "admin/districts/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("districtForm", adminDistrictService.editForm(id));
        addReferenceData(model);
        return "admin/districts/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("districtForm") DistrictForm districtForm, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            addReferenceData(model);
            return "admin/districts/form";
        }
        adminDistrictService.save(districtForm);
        return "redirect:/admin/districts";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("districtForm") DistrictForm districtForm,
            BindingResult bindingResult, Model model) {
        districtForm.setId(id);
        if (bindingResult.hasErrors()) {
            addReferenceData(model);
            return "admin/districts/form";
        }
        adminDistrictService.save(districtForm);
        return "redirect:/admin/districts";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminDistrictService.delete(id);
        return "redirect:/admin/districts";
    }

    private void addReferenceData(Model model) {
        model.addAttribute("chapters", chapterRepository.findAll());
        model.addAttribute("npcs", npcRepository.findAll());
    }
}
