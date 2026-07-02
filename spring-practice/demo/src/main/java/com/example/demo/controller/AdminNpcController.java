package com.example.demo.controller;

import com.example.demo.dto.NpcForm;
import com.example.demo.repository.ChapterRepository;
import com.example.demo.service.AdminNpcService;
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
@RequestMapping("/admin/npcs")
@RequiredArgsConstructor
public class AdminNpcController {

    private final AdminNpcService adminNpcService;
    private final ChapterRepository chapterRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("npcs", adminNpcService.findAll());
        return "admin/npcs/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("npcForm", adminNpcService.newForm());
        addReferenceData(model);
        return "admin/npcs/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("npcForm", adminNpcService.editForm(id));
        addReferenceData(model);
        return "admin/npcs/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("npcForm") NpcForm npcForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            addReferenceData(model);
            return "admin/npcs/form";
        }
        adminNpcService.save(npcForm);
        return "redirect:/admin/npcs";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("npcForm") NpcForm npcForm,
            BindingResult bindingResult, Model model) {
        npcForm.setId(id);
        if (bindingResult.hasErrors()) {
            addReferenceData(model);
            return "admin/npcs/form";
        }
        adminNpcService.save(npcForm);
        return "redirect:/admin/npcs";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminNpcService.delete(id);
        return "redirect:/admin/npcs";
    }

    private void addReferenceData(Model model) {
        model.addAttribute("chapters", chapterRepository.findAll());
    }
}
