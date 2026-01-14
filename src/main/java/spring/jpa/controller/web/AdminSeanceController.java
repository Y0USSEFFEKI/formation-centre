package spring.jpa.controller.web;

import java.util.Collections;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import spring.jpa.entity.Cours;
import spring.jpa.entity.Groupe;
import spring.jpa.entity.Seance;
import spring.jpa.service.CoursService;
import spring.jpa.service.FormateurService;
import spring.jpa.service.GroupeService;
import spring.jpa.service.SeanceService;

@Controller
@RequestMapping("/admin/seances")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSeanceController {

    private final SeanceService seanceService;
    private final CoursService coursService;
    private final FormateurService formateurService;
    private final GroupeService groupeService;

    public AdminSeanceController(SeanceService seanceService,
                                 CoursService coursService,
                                 FormateurService formateurService,
                                 GroupeService groupeService) {
        this.seanceService = seanceService;
        this.coursService = coursService;
        this.formateurService = formateurService;
        this.groupeService = groupeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("seances", seanceService.listAll());
        return "admin/seances/list";
    }

    @GetMapping("/create")
    public String createForm(@RequestParam(required = false) Long coursId, Model model) {
        Seance seance = new Seance();
        Cours selectedCours = coursId != null ? coursService.getById(coursId) : null;
        seance.setCours(selectedCours);

        model.addAttribute("seance", seance);
        model.addAttribute("coursList", coursService.listAll());
        model.addAttribute("formateurs", formateurService.listAll());
        model.addAttribute("groupes", groupesForCours(selectedCours));
        model.addAttribute("formTitle", "Ajouter une seance");
        return "admin/seances/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id,
                           @RequestParam(required = false) Long coursId,
                           Model model) {
        Seance seance = seanceService.getById(id);
        Cours selectedCours = coursId != null ? coursService.getById(coursId) : seance.getCours();
        seance.setCours(selectedCours);

        model.addAttribute("seance", seance);
        model.addAttribute("coursList", coursService.listAll());
        model.addAttribute("formateurs", formateurService.listAll());
        model.addAttribute("groupes", groupesForCours(selectedCours));
        model.addAttribute("formTitle", "Modifier une seance");
        return "admin/seances/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Seance seance, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("coursList", coursService.listAll());
            model.addAttribute("formateurs", formateurService.listAll());
            model.addAttribute("groupes", groupesForCours(seance.getCours()));
            model.addAttribute("formTitle",
                seance.getId() == null ? "Ajouter une seance" : "Modifier une seance");
            return "admin/seances/form";
        }

        if (seance.getId() == null) {
            seanceService.create(seance);
        } else {
            seanceService.update(seance.getId(), seance);
        }
        return "redirect:/admin/seances";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        seanceService.delete(id);
        return "redirect:/admin/seances";
    }

    private List<Groupe> groupesForCours(Cours cours) {
        if (cours == null || cours.getSpecialty() == null) {
            return Collections.emptyList();
        }
        return groupeService.listBySpecialite(cours.getSpecialty().getId());
    }
}
