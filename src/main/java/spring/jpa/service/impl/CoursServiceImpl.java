package spring.jpa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import spring.jpa.entity.Cours;
import spring.jpa.repository.CoursRepository;
import spring.jpa.service.CoursService;

@Service
public class CoursServiceImpl implements CoursService {

    private final CoursRepository repo;

    public CoursServiceImpl(CoursRepository repo) {
        this.repo = repo;
    }

    @Override
    public Cours create(Cours c) {
        return repo.save(c);
    }

    @Override
    public Cours update(Long id, Cours c) {
        Cours existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cours introuvable"));

        existing.setCode(c.getCode());
        existing.setTitle(c.getTitle());
        existing.setHours(c.getHours());
        existing.setSpecialty(c.getSpecialty());
        existing.setFormateur(c.getFormateur());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Cours introuvable");
        }
        repo.deleteById(id);
    }

    @Override
    public Cours getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cours introuvable"));
    }

    @Override
    public List<Cours> listAll() {
        return repo.findAll();
    }
}
