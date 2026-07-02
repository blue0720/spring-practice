package com.example.demo.controller;

import com.example.demo.dto.MemoryFragmentForm;
import com.example.demo.service.AdminMemoryFragmentService;
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
@RequestMapping("/admin/memories")
@RequiredArgsConstructor
public class AdminMemoryFragmentController {

    private final AdminMemoryFragmentService adminMemoryFragmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("memories", adminMemoryFragmentService.findAll());
        return "admin/memories/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("memoryFragmentForm", adminMemoryFragmentService.newForm());
        return "admin/memories/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("memoryFragmentForm", adminMemoryFragmentService.editForm(id));
        return "admin/memories/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("memoryFragmentForm") MemoryFragmentForm memoryFragmentForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/memories/form";
        }
        adminMemoryFragmentService.save(memoryFragmentForm);
        return "redirect:/admin/memories";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("memoryFragmentForm") MemoryFragmentForm memoryFragmentForm,
            BindingResult bindingResult) {
        memoryFragmentForm.setId(id);
        if (bindingResult.hasErrors()) {
            return "admin/memories/form";
        }
        adminMemoryFragmentService.save(memoryFragmentForm);
        return "redirect:/admin/memories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminMemoryFragmentService.delete(id);
        return "redirect:/admin/memories";
    }
}
