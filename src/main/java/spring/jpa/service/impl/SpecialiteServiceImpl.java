package spring.jpa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import spring.jpa.entity.Specialite;
import spring.jpa.repository.SpecialiteRepository;
import spring.jpa.service.SpecialiteService;

@Service
public class SpecialiteServiceImpl implements SpecialiteService {

    private final SpecialiteRepository repo;

    public SpecialiteServiceImpl(SpecialiteRepository repo) {
        this.repo = repo;
    }

    @Override
    public Specialite create(Specialite s) {
        if (repo.existsByName(s.getName())) {
            throw new IllegalArgumentException("Specialite existante");
        }
        return repo.save(s);
    }

    @Override
    public Specialite update(Long id, Specialite s) {
        Specialite existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Specialite introuvable"));

        if (!existing.getName().equals(s.getName()) && repo.existsByName(s.getName())) {
            throw new IllegalArgumentException("Specialite existante");
        }

        existing.setName(s.getName());
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Specialite introuvable");
        }
        repo.deleteById(id);
    }

    @Override
    public Specialite getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Specialite introuvable"));
    }

    @Override
    public List<Specialite> listAll() {
        return repo.findAll();
    }
}
