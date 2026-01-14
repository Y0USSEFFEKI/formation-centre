package spring.jpa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import spring.jpa.entity.Formateur;
import spring.jpa.repository.FormateurRepository;
import spring.jpa.service.FormateurService;

@Service
public class FormateurServiceImpl implements FormateurService {

    private final FormateurRepository repo;

    public FormateurServiceImpl(FormateurRepository repo) {
        this.repo = repo;
    }

    @Override
    public Formateur create(Formateur f) {
        return repo.save(f);
    }

    @Override
    public Formateur update(Long id, Formateur f) {
        Formateur existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Formateur introuvable"));

        existing.setPrenom(f.getPrenom());
        existing.setNom(f.getNom());
        existing.setEmail(f.getEmail());
        existing.setSpecialties(f.getSpecialties());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Formateur introuvable");
        }
        repo.deleteById(id);
    }

    @Override
    public Formateur getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Formateur introuvable"));
    }

    @Override
    public List<Formateur> listAll() {
        return repo.findAll();
    }
}
