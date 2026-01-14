package spring.jpa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import spring.jpa.entity.Seance;
import spring.jpa.repository.SeanceRepository;
import spring.jpa.service.SeanceService;

@Service
public class SeanceServiceImpl implements SeanceService {

    private final SeanceRepository repo;

    public SeanceServiceImpl(SeanceRepository repo) {
        this.repo = repo;
    }

    @Override
    public Seance create(Seance s) {
        return repo.save(s);
    }

    @Override
    public Seance update(Long id, Seance s) {
        Seance existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seance introuvable"));

        existing.setCours(s.getCours());
        existing.setFormateur(s.getFormateur());
        existing.setGroupe(s.getGroupe());
        existing.setHeureDebut(s.getHeureDebut());
        existing.setHeureFin(s.getHeureFin());
        existing.setSalle(s.getSalle());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Seance introuvable");
        }
        repo.deleteById(id);
    }

    @Override
    public Seance getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seance introuvable"));
    }

    @Override
    public List<Seance> listAll() {
        return repo.findAll();
    }
}
