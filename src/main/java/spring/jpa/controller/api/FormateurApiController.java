package spring.jpa.controller.api;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import spring.jpa.dto.CoursDto;
import spring.jpa.dto.FormateurProfileDto;
import spring.jpa.dto.GroupeDto;
import spring.jpa.entity.Formateur;
import spring.jpa.entity.Seance;
import spring.jpa.dto.SeanceDto;
import java.util.Comparator;
import spring.jpa.repository.CoursRepository;
import spring.jpa.repository.FormateurRepository;
import spring.jpa.repository.GroupeRepository;
import spring.jpa.repository.SeanceRepository;
import spring.jpa.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/formateur")
public class FormateurApiController {

    private final FormateurRepository formateurRepository;
    private final CoursRepository coursRepository;
    private final GroupeRepository groupeRepository;
    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(FormateurApiController.class);

    public FormateurApiController(FormateurRepository formateurRepository,
                                  CoursRepository coursRepository,
                                  GroupeRepository groupeRepository,
                                  SeanceRepository seanceRepository,
                                  UserRepository userRepository) {
        this.formateurRepository = formateurRepository;
        this.coursRepository = coursRepository;
        this.groupeRepository = groupeRepository;
        this.seanceRepository = seanceRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public FormateurProfileDto me() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        Formateur f = findFormateurByUsername(username);

        FormateurProfileDto dto = new FormateurProfileDto();
        dto.setId(f.getId());
        dto.setPrenom(f.getPrenom());
        dto.setNom(f.getNom());
        dto.setEmail(f.getEmail());
        return dto;
    }

    @PutMapping("/me")
    public FormateurProfileDto update(@RequestBody FormateurProfileDto req) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        Formateur f = findFormateurByUsername(username);

        f.setPrenom(req.getPrenom());
        f.setNom(req.getNom());
        f.setEmail(req.getEmail());
        formateurRepository.save(f);

        return me();
    }

    @GetMapping("/cours")
    public List<CoursDto> mesCours() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        Formateur f = findFormateurByUsername(username);

        return coursRepository.findByFormateur_Id(f.getId()).stream().map(c -> {
            CoursDto dto = new CoursDto();
            dto.setId(c.getId());
            dto.setCode(c.getCode());
            dto.setTitle(c.getTitle());
            return dto;
        }).toList();
    }

    @GetMapping("/seances")
    public List<?> seances() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        Formateur f = findFormateurByUsername(username);
        return seanceRepository.findByFormateur_Id(f.getId());
    }

    @GetMapping("/groupes")
    public List<GroupeDto> mesGroupes() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Première tentative : lien direct user -> formateur
        var optFormateur = formateurRepository.findByUser_Username(username);
        Formateur f = null;
        if (optFormateur.isPresent()) {
            f = optFormateur.get();
            log.info("Found formateur by user.username={} -> formateur.id={}", username, f.getId());
        } else {
            // Fallback : essayer de retrouver le formateur à partir du fullname
            var user = userRepository.findByUsername(username).orElse(null);
            if (user != null && user.getFullname() != null) {
                String[] parts = user.getFullname().split(" ", 2);
                String prenom = parts.length > 0 ? parts[0].trim() : "";
                String nom = parts.length > 1 ? parts[1].trim() : "";
                if (!prenom.isBlank() && !nom.isBlank()) {
                    var alt = formateurRepository.findByPrenomAndNom(prenom, nom);
                    if (alt.isPresent()) {
                        f = alt.get();
                        log.info("Fallback: found formateur by name {} {} -> id={}", prenom, nom, f.getId());
                    }
                }
            }
        }

        if (f == null) {
            log.warn("No formateur found for user '{}', returning empty groupes list", username);
            return java.util.Collections.emptyList();
        }

        // Logique demandée : prendre le PREMIER cours du formateur et utiliser sa spécialité
        var coursList = coursRepository.findByFormateur_Id(f.getId());
        if (coursList == null || coursList.isEmpty()) {
            log.info("Formateur id={} has no cours", f.getId());
            return java.util.Collections.emptyList();
        }

        var firstCourse = coursList.get(0);
        if (firstCourse == null || firstCourse.getSpecialty() == null || firstCourse.getSpecialty().getId() == null) {
            log.info("First course of formateur id={} has no specialty", f.getId());
            return java.util.Collections.emptyList();
        }

        Long specialiteId = firstCourse.getSpecialty().getId();

        // Récupérer uniquement les groupes ayant specialite_id == specialiteId
        var groupes = groupeRepository.findBySpecialite_Id(specialiteId);
        log.info("Returning {} groupes for specialite id={}", groupes.size(), specialiteId);
        return groupes.stream()
            .map(groupe -> {
                GroupeDto dto = new GroupeDto();
                dto.setId(groupe.getId());
                dto.setNom(groupe.getNom());
                if (groupe.getSpecialite() != null) {
                    dto.setSpecialiteId(groupe.getSpecialite().getId());
                    dto.setSpecialiteName(groupe.getSpecialite().getName());
                }
                return dto;
            })
            .toList();
    }

    @GetMapping("/emploi-du-temps")
    public List<SeanceDto> emploiDuTemps() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        Formateur f = findFormateurByUsername(username);

        return seanceRepository.findByFormateur_Id(f.getId()).stream()
            .sorted(Comparator.comparing(Seance::getHeureDebut))
            .map(s -> {
                SeanceDto dto = new SeanceDto();
                dto.setId(s.getId());
                dto.setHeureDebut(s.getHeureDebut());
                dto.setHeureFin(s.getHeureFin());
                dto.setSalle(s.getSalle());

                SeanceDto.CoursSummary cours = new SeanceDto.CoursSummary();
                if (s.getCours() != null) {
                    cours.setId(s.getCours().getId());
                    cours.setCode(s.getCours().getCode());
                }
                String title = (s.getGroupe() != null && s.getGroupe().getNom() != null)
                    ? s.getGroupe().getNom()
                    : (s.getCours() != null ? s.getCours().getTitle() : null);
                cours.setTitle(title);
                dto.setCours(cours);
                return dto;
            }).toList();
    }

    private Formateur findFormateurByUsername(String username) {
        return formateurRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Formateur not found for user " + username));
    }
}
