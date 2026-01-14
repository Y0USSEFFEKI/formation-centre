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
import spring.jpa.entity.Formateur;
import spring.jpa.service.FormateurService;
import spring.jpa.service.SpecialiteService;

@Controller
@RequestMapping("/admin/formateurs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFormateurController {

    private final FormateurService formateurService;
    private final SpecialiteService specialiteService;

    public AdminFormateurController(FormateurService formateurService, SpecialiteService specialiteService) {
        this.formateurService = formateurService;
        this.specialiteService = specialiteService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("formateurs", formateurService.listAll());
        return "admin/formateurs/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("formateur", new Formateur());
        model.addAttribute("specialites", specialiteService.listAll());
        model.addAttribute("formTitle", "Ajouter un formateur");
        return "admin/formateurs/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("formateur", formateurService.getById(id));
        model.addAttribute("specialites", specialiteService.listAll());
        model.addAttribute("formTitle", "Modifier un formateur");
        return "admin/formateurs/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Formateur formateur, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("specialites", specialiteService.listAll());
            model.addAttribute("formTitle",
                formateur.getId() == null ? "Ajouter un formateur" : "Modifier un formateur");
            return "admin/formateurs/form";
        }

        if (formateur.getId() == null) {
            formateurService.create(formateur);
        } else {
            formateurService.update(formateur.getId(), formateur);
        }
        return "redirect:/admin/formateurs";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        formateurService.delete(id);
        return "redirect:/admin/formateurs";
    }
}
