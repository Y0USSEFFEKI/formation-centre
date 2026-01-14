package spring.jpa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import spring.jpa.entity.Note;
import spring.jpa.repository.NoteRepository;
import spring.jpa.service.NoteService;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository repo;

    public NoteServiceImpl(NoteRepository repo) {
        this.repo = repo;
    }

    @Override
    public Note create(Note n) {
        return repo.save(n);
    }

    @Override
    public Note update(Long id, Note n) {
        Note existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note introuvable"));

        existing.setEtudiant(n.getEtudiant());
        existing.setCours(n.getCours());
        existing.setValeur(n.getValeur());
        existing.setType(n.getType());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Note getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note introuvable"));
    }

    @Override
    public List<Note> listAll() {
        return repo.findAll();
    }

    @Override
    public List<Note> listByCoursAndGroupe(Long coursId, Long groupeId) {
        return repo.findByCours_IdAndEtudiant_Groupe_Id(coursId, groupeId);
    }
}
