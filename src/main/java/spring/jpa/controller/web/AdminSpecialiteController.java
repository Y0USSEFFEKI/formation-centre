package spring.jpa.controller.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import spring.jpa.entity.Specialite;
import spring.jpa.service.SpecialiteService;

@Controller
@RequestMapping("/admin/specialites")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSpecialiteController {

    private final SpecialiteService specialiteService;

    public AdminSpecialiteController(SpecialiteService specialiteService) {
        this.specialiteService = specialiteService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("specialites", specialiteService.listAll());
        return "admin/specialites/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("specialite", new Specialite());
        model.addAttribute("formTitle", "Ajouter une specialite");
        return "admin/specialites/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("specialite", specialiteService.getById(id));
        model.addAttribute("formTitle", "Modifier une specialite");
        return "admin/specialites/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Specialite specialite, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("formTitle",
                specialite.getId() == null ? "Ajouter une specialite" : "Modifier une specialite");
            return "admin/specialites/form";
        }
        try {
            if (specialite.getId() == null) {
                specialiteService.create(specialite);
            } else {
                specialiteService.update(specialite.getId(), specialite);
            }
            return "redirect:/admin/specialites";
        } catch (IllegalArgumentException ex) {
            br.rejectValue("name", "name.duplicate", ex.getMessage());
            model.addAttribute("formTitle",
                specialite.getId() == null ? "Ajouter une specialite" : "Modifier une specialite");
            return "admin/specialites/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        specialiteService.delete(id);
        return "redirect:/admin/specialites";
    }
}
