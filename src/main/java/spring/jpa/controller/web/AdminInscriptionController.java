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
import spring.jpa.entity.Inscription;
import spring.jpa.service.CoursService;
import spring.jpa.service.EtudiantService;
import spring.jpa.service.InscriptionService;

@Controller
@RequestMapping("/admin/inscriptions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminInscriptionController {

    private final InscriptionService inscriptionService;
    private final EtudiantService etudiantService;
    private final CoursService coursService;

    public AdminInscriptionController(InscriptionService inscriptionService,
                                      EtudiantService etudiantService,
                                      CoursService coursService) {
        this.inscriptionService = inscriptionService;
        this.etudiantService = etudiantService;
        this.coursService = coursService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("inscriptions", inscriptionService.listAll());
        return "admin/inscriptions/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("inscription", new Inscription());
        model.addAttribute("etudiants", etudiantService.listAll());
        model.addAttribute("coursList", coursService.listAll());
        model.addAttribute("formTitle", "Ajouter une inscription");
        return "admin/inscriptions/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("inscription", inscriptionService.getById(id));
        model.addAttribute("etudiants", etudiantService.listAll());
        model.addAttribute("coursList", coursService.listAll());
        model.addAttribute("formTitle", "Modifier une inscription");
        return "admin/inscriptions/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Inscription inscription,
                       BindingResult br,
                       Model model) {
        if (br.hasErrors()) {
            model.addAttribute("etudiants", etudiantService.listAll());
            model.addAttribute("coursList", coursService.listAll());
            model.addAttribute("formTitle",
                inscription.getId() == null ? "Ajouter une inscription" : "Modifier une inscription");
            return "admin/inscriptions/form";
        }

        if (inscription.getId() == null) {
            inscriptionService.create(inscription);
        } else {
            inscriptionService.update(inscription.getId(), inscription);
        }
        return "redirect:/admin/inscriptions";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        inscriptionService.delete(id);
        return "redirect:/admin/inscriptions";
    }
}
