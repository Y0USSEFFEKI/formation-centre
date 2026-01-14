package spring.jpa.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import spring.jpa.entity.Groupe;
import spring.jpa.repository.GroupeRepository;
import spring.jpa.service.GroupeService;

@Service
public class GroupeServiceImpl implements GroupeService {

    private final GroupeRepository repo;

    public GroupeServiceImpl(GroupeRepository repo) {
        this.repo = repo;
    }

    @Override
    public Groupe create(Groupe g) {
        // règle métier : nom unique
        if (repo.existsByNom(g.getNom())) {
            throw new IllegalArgumentException("Ce nom de groupe existe déjà");
        }
        return repo.save(g);
    }

    @Override
    public Groupe update(Long id, Groupe g) {
        Groupe existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Groupe introuvable"));

        existing.setNom(g.getNom());
        existing.setSpecialite(g.getSpecialite());
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Groupe findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Groupe introuvable"));
    }

    @Override
    public List<Groupe> listAll() {
        return repo.findAll();
    }
    
    @Override
    public List<Groupe> listBySpecialite(Long specialiteId) {
        if (specialiteId == null) {
            return Collections.emptyList();
        }
        return repo.findBySpecialite_Id(specialiteId);
    }
}
