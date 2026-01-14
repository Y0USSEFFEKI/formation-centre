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
import spring.jpa.entity.Note;
import spring.jpa.service.CoursService;
import spring.jpa.service.EtudiantService;
import spring.jpa.service.GroupeService;
import spring.jpa.service.NoteService;
import spring.jpa.service.SpecialiteService;

@Controller
@RequestMapping("/admin/notes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNoteController {

    private final NoteService noteService;
    private final SpecialiteService specialiteService;
    private final CoursService coursService;
    private final GroupeService groupeService;
    private final EtudiantService etudiantService;

    public AdminNoteController(NoteService noteService,
                               SpecialiteService specialiteService,
                               CoursService coursService,
                               GroupeService groupeService,
                               EtudiantService etudiantService) {
        this.noteService = noteService;
        this.specialiteService = specialiteService;
        this.coursService = coursService;
        this.groupeService = groupeService;
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long specialiteId,
                       @RequestParam(required = false) Long coursId,
                       @RequestParam(required = false) Long groupeId,
                       Model model) {

        model.addAttribute("specialites", specialiteService.listAll());
        model.addAttribute("coursAll", coursService.listAll());
        model.addAttribute("groupesAll", groupeService.listAll());

        boolean showNotes = coursId != null && groupeId != null;
        model.addAttribute("showNotes", showNotes);
        model.addAttribute("notes",
                showNotes ? noteService.listByCoursAndGroupe(coursId, groupeId) : Collections.emptyList());

        model.addAttribute("selectedSpecialiteId", specialiteId);
        model.addAttribute("selectedCoursId", coursId);
        model.addAttribute("selectedGroupeId", groupeId);

        return "admin/notes/list";
    }

    @GetMapping("/create")
    public String createForm(@RequestParam Long coursId,
                             @RequestParam Long groupeId,
                             Model model) {

        Cours cours = coursService.getById(coursId);
        Groupe groupe = groupeService.findById(groupeId);

        Note note = new Note();
        note.setCours(cours);

        model.addAttribute("note", note);
        model.addAttribute("cours", cours);
        model.addAttribute("groupe", groupe);
        model.addAttribute("coursId", coursId);
        model.addAttribute("groupeId", groupeId);
        model.addAttribute("etudiants", etudiantService.listByGroupe(groupeId));
        model.addAttribute("formTitle", "Ajouter une note");

        return "admin/notes/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Note note = noteService.getById(id);
        Cours cours = note.getCours();
        Groupe groupe = note.getEtudiant() != null ? note.getEtudiant().getGroupe() : null;
        Long groupeId = groupe != null ? groupe.getId() : null;

        model.addAttribute("note", note);
        model.addAttribute("cours", cours);
        model.addAttribute("groupe", groupe);
        model.addAttribute("coursId", cours != null ? cours.getId() : null);
        model.addAttribute("groupeId", groupeId);
        model.addAttribute("etudiants",
                groupeId != null ? etudiantService.listByGroupe(groupeId) : Collections.emptyList());
        model.addAttribute("formTitle", "Modifier une note");

        return "admin/notes/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Note note,
                       BindingResult br,
                       @RequestParam Long coursId,
                       @RequestParam Long groupeId,
                       Model model) {

        if (br.hasErrors()) {
            Cours cours = coursService.getById(coursId);
            Groupe groupe = groupeService.findById(groupeId);

            note.setCours(cours);

            model.addAttribute("cours", cours);
            model.addAttribute("groupe", groupe);
            model.addAttribute("coursId", coursId);
            model.addAttribute("groupeId", groupeId);
            model.addAttribute("etudiants", etudiantService.listByGroupe(groupeId));
            model.addAttribute("formTitle",
                note.getId() == null ? "Ajouter une note" : "Modifier une note");
            return "admin/notes/form";
        }

        note.setCours(coursService.getById(coursId));

        if (note.getId() == null) {
            noteService.create(note);
        } else {
            noteService.update(note.getId(), note);
        }
        return "redirect:/admin/notes?coursId=" + coursId + "&groupeId=" + groupeId;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         @RequestParam(required = false) Long coursId,
                         @RequestParam(required = false) Long groupeId) {
        noteService.delete(id);
        if (coursId != null && groupeId != null) {
            return "redirect:/admin/notes?coursId=" + coursId + "&groupeId=" + groupeId;
        }
        return "redirect:/admin/notes";
    }
}
