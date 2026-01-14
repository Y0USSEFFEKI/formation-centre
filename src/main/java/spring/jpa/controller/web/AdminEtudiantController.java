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
import spring.jpa.entity.Etudiant;
import spring.jpa.service.EtudiantService;
import spring.jpa.service.GroupeService;

@Controller
@RequestMapping("/admin/etudiants")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEtudiantController {
  private final EtudiantService etudiantService;
  private final GroupeService groupeService;
  public AdminEtudiantController(EtudiantService etudiantService, GroupeService groupeService){ this.etudiantService = etudiantService;
  this.groupeService = groupeService; }
  
  @GetMapping
  public String list(Model model){
    model.addAttribute("etudiants", etudiantService.listAll());
    return "admin/etudiants/list";
  }
  @GetMapping("/create") public String createForm(Model model){
    model.addAttribute("etudiant", new Etudiant());
    model.addAttribute("groupes", groupeService.listAll()); 
    model.addAttribute("formTitle", "Ajouter un étudiant");
    return "admin/etudiants/form";
  }
  @GetMapping("/edit/{id}")
  public String editForm(@PathVariable Long id, Model model) {
      model.addAttribute("etudiant", etudiantService.getById(id));
      model.addAttribute("groupes", groupeService.listAll());
      model.addAttribute("formTitle", "Modifier un etudiant");
      return "admin/etudiants/form";
  }
  @PostMapping("/save")
  public String save(@Valid @ModelAttribute Etudiant etudiant, BindingResult br, Model model) {
      if (br.hasErrors()) {
          model.addAttribute("groupes", groupeService.listAll());
          model.addAttribute("formTitle",
              etudiant.getId() == null ? "Ajouter un etudiant" : "Modifier un etudiant");
          return "admin/etudiants/form";
      }
      try {
          if (etudiant.getId() == null) {
              etudiantService.create(etudiant);
          } else {
              etudiantService.update(etudiant.getId(), etudiant);
          }
          return "redirect:/admin/etudiants";
      } catch (IllegalArgumentException ex) {
          br.rejectValue("matricule", "matricule.duplicate", ex.getMessage());
          model.addAttribute("groupes", groupeService.listAll());
          model.addAttribute("formTitle",
              etudiant.getId() == null ? "Ajouter un etudiant" : "Modifier un etudiant");
          return "admin/etudiants/form";
      }
  }
  @GetMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
      etudiantService.delete(id);
      return "redirect:/admin/etudiants";
  }
}

