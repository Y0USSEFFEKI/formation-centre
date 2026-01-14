package spring.jpa.controller.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import spring.jpa.dto.CoursDto;
import spring.jpa.dto.EtudiantProfileDto;
import spring.jpa.dto.InscriptionRequest;
import spring.jpa.dto.NoteDto;
import spring.jpa.dto.SeanceDto;
import spring.jpa.entity.Inscription;
import spring.jpa.repository.CoursRepository;
import spring.jpa.repository.EtudiantRepository;
import spring.jpa.repository.InscriptionRepository;
import spring.jpa.repository.NoteRepository;
import spring.jpa.repository.SeanceRepository;

@RestController
@RequestMapping("/api/etudiant")
public class EtudiantApiController {

    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;
    private final InscriptionRepository inscriptionRepository;
    private final NoteRepository noteRepository;
    private final SeanceRepository seanceRepository;

    public EtudiantApiController(EtudiantRepository etudiantRepository,
                                 CoursRepository coursRepository,
                                 InscriptionRepository inscriptionRepository,
                                 NoteRepository noteRepository,
                                 SeanceRepository seanceRepository) {
        this.etudiantRepository = etudiantRepository;
        this.coursRepository = coursRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.noteRepository = noteRepository;
        this.seanceRepository = seanceRepository;
    }

    @GetMapping("/me")
    public EtudiantProfileDto me() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var e = etudiantRepository.findByUser_Username(username).orElseThrow();

        EtudiantProfileDto dto = new EtudiantProfileDto();
        dto.setId(e.getId());
        dto.setPrenom(e.getPrenom());
        dto.setNom(e.getNom());
        dto.setEmail(e.getEmail());
        if (e.getGroupe() != null) {
            dto.setGroupeId(e.getGroupe().getId());
            dto.setGroupeNom(e.getGroupe().getNom());
        }
        return dto;
    }

    @PutMapping("/me")
    public EtudiantProfileDto update(@RequestBody EtudiantProfileDto req) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var e = etudiantRepository.findByUser_Username(username).orElseThrow();

        e.setPrenom(req.getPrenom());
        e.setNom(req.getNom());
        e.setEmail(req.getEmail());
        etudiantRepository.save(e);

        return me();
    }

    @GetMapping("/cours")
    public List<CoursDto> mesCours() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var e = etudiantRepository.findByUser_Username(username).orElseThrow();

        return inscriptionRepository.findByEtudiant_Id(e.getId()).stream()
            .map(i -> i.getCours())
            .filter(c -> c != null)
            .map(c -> {
                CoursDto dto = new CoursDto();
                dto.setId(c.getId());
                dto.setCode(c.getCode());
                dto.setTitle(c.getTitle());
                if (c.getSpecialty() != null) {
                    dto.setSpecialiteId(c.getSpecialty().getId());
                    dto.setSpecialiteName(c.getSpecialty().getName());
                }
                return dto;
            }).toList();
    }

    @GetMapping("/notes")
    public List<NoteDto> mesNotes() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var e = etudiantRepository.findByUser_Username(username).orElseThrow();

        return noteRepository.findByEtudiant_Id(e.getId()).stream()
            .map(note -> {
                NoteDto dto = new NoteDto();
                dto.setId(note.getId());
                if (note.getCours() != null) {
                    dto.setCoursId(note.getCours().getId());
                }
                dto.setValeur(note.getValeur());
                dto.setType(note.getType());
                return dto;
            })
            .toList();
    }

    @PostMapping("/inscriptions")
    public void inscrire(@RequestBody InscriptionRequest req) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var e = etudiantRepository.findByUser_Username(username).orElseThrow();
        var cours = coursRepository.findById(req.getCoursId()).orElseThrow();

        if (inscriptionRepository.existsByEtudiant_IdAndCours_Id(e.getId(), cours.getId())) {
            return;
        }

        Inscription i = new Inscription();
        i.setEtudiant(e);
        i.setCours(cours);
        i.setDate(LocalDate.now());
        inscriptionRepository.save(i);
    }

    @DeleteMapping("/inscriptions/{coursId}")
    public void annulerInscription(@PathVariable Long coursId) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var e = etudiantRepository.findByUser_Username(username).orElseThrow();

        inscriptionRepository.deleteByEtudiantIdAndCoursId(e.getId(), coursId);
    }

    @GetMapping("/emploi-du-temps")
    public List<SeanceDto> emploiDuTemps() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var e = etudiantRepository.findByUser_Username(username).orElseThrow();

        Long groupeId = e.getGroupe() != null ? e.getGroupe().getId() : null;
        if (groupeId == null) {
            return List.of();
        }

        var coursIds = inscriptionRepository.findByEtudiant_Id(e.getId()).stream()
            .map(i -> i.getCours())
            .filter(c -> c != null)
            .map(c -> c.getId())
            .distinct()
            .toList();

        if (coursIds.isEmpty()) {
            return List.of();
        }

        // Query DB for seances matching both groupe id and cours ids to avoid returning
        // seances from other groupes (and avoid relying on in-memory filtering).
        return seanceRepository.findByGroupe_IdAndCours_IdInOrderByHeureDebutAsc(groupeId, coursIds).stream()
            .map(s -> {
                SeanceDto dto = new SeanceDto();
                dto.setId(s.getId());
                dto.setHeureDebut(s.getHeureDebut());
                dto.setHeureFin(s.getHeureFin());
                dto.setSalle(s.getSalle());

                if (s.getCours() != null) {
                    SeanceDto.CoursSummary cours = new SeanceDto.CoursSummary();
                    cours.setId(s.getCours().getId());
                    cours.setCode(s.getCours().getCode());
                    cours.setTitle(s.getCours().getTitle());
                    dto.setCours(cours);
                }
                return dto;
            })
            .toList();
    }
}
