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
import spring.jpa.entity.Groupe;
import spring.jpa.service.GroupeService;
import spring.jpa.service.SpecialiteService;

@Controller
@RequestMapping("/admin/groupes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminGroupeController {

    private final GroupeService groupeService;
    private final SpecialiteService specialiteService;

    public AdminGroupeController(GroupeService groupeService, SpecialiteService specialiteService) {
        this.groupeService = groupeService;
        this.specialiteService = specialiteService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("groupes", groupeService.listAll());
        return "admin/groupes/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("groupe", new Groupe());
        model.addAttribute("specialites", specialiteService.listAll());
        model.addAttribute("formTitle", "Ajouter un groupe");
        return "admin/groupes/form";
    }
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("groupe", groupeService.findById(id));
        model.addAttribute("specialites", specialiteService.listAll());
        model.addAttribute("formTitle", "Modifier un groupe");
        return "admin/groupes/form";
    }
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Groupe groupe, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("specialites", specialiteService.listAll());
            return "admin/groupes/form";
        }
        try {
            if (groupe.getId() == null) {
                groupeService.create(groupe);
            } else {
            	groupeService.update(groupe.getId(), groupe);
            }
            return "redirect:/admin/groupes";
        } catch (IllegalArgumentException ex) {
            br.rejectValue("matricule", "matricule.duplicate", ex.getMessage());
            model.addAttribute("groupes", groupeService.listAll());
            model.addAttribute("formTitle",
            		groupe.getId() == null ? "Ajouter un groupe" : "Modifier un groupe");
            return "admin/groupes/form";
        }
    }
}
