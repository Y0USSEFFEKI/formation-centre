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
import spring.jpa.entity.Cours;
import spring.jpa.service.CoursService;
import spring.jpa.service.FormateurService;
import spring.jpa.service.SpecialiteService;

@Controller
@RequestMapping("/admin/cours")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCoursController {

    private final CoursService coursService;
    private final SpecialiteService specialiteService;
    private final FormateurService formateurService;

    public AdminCoursController(CoursService coursService,
            SpecialiteService specialiteService,
            FormateurService formateurService) {
		this.coursService = coursService;
		this.specialiteService = specialiteService;
		this.formateurService = formateurService;
	}

    @GetMapping
    public String list(Model model) {
        model.addAttribute("cours", coursService.listAll());
        return "admin/cours/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("cours", new Cours());
        model.addAttribute("specialites", specialiteService.listAll());
        model.addAttribute("formateurs", formateurService.listAll());
        model.addAttribute("formTitle", "Ajouter un cours");
        return "admin/cours/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("cours", coursService.getById(id));
        model.addAttribute("specialites", specialiteService.listAll());
        model.addAttribute("formateurs", formateurService.listAll());
        model.addAttribute("formTitle", "Modifier un cours");
        return "admin/cours/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Cours cours, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("specialites", specialiteService.listAll());
            model.addAttribute("formateurs", formateurService.listAll());
            model.addAttribute("formTitle",
                cours.getId() == null ? "Ajouter un cours" : "Modifier un cours");
            return "admin/cours/form";
        }
        if (cours.getId() == null) {
            coursService.create(cours);
        } else {
            coursService.update(cours.getId(), cours);
        }
        return "redirect:/admin/cours";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        coursService.delete(id);
        return "redirect:/admin/cours";
    }
}
