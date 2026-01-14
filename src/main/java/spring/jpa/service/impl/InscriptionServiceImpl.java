package spring.jpa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import spring.jpa.entity.Inscription;
import spring.jpa.repository.InscriptionRepository;
import spring.jpa.service.InscriptionService;

@Service
public class InscriptionServiceImpl implements InscriptionService {

    private final InscriptionRepository repo;

    public InscriptionServiceImpl(InscriptionRepository repo) {
        this.repo = repo;
    }

    @Override
    public Inscription create(Inscription i) {
        return repo.save(i);
    }

    @Override
    public Inscription update(Long id, Inscription i) {
        Inscription existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscription introuvable"));

        existing.setEtudiant(i.getEtudiant());
        existing.setCours(i.getCours());
        existing.setDate(i.getDate());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Inscription getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscription introuvable"));
    }

    @Override
    public List<Inscription> listAll() {
        return repo.findAll();
    }
}
