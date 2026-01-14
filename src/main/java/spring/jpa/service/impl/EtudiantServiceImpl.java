package spring.jpa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import spring.jpa.entity.Etudiant;
import spring.jpa.repository.EtudiantRepository;
import spring.jpa.service.EtudiantService;

@Service
public class EtudiantServiceImpl implements EtudiantService {

    private final EtudiantRepository repo;

    public EtudiantServiceImpl(EtudiantRepository repo) {
        this.repo = repo;
    }

    @Override
    public Etudiant create(Etudiant etudiant) {
        // Règle métier : unicité du matricule
        if (repo.findByMatricule(etudiant.getMatricule()).isPresent()) {
            throw new IllegalArgumentException("Matricule existant");
        }
        return repo.save(etudiant);
    }

    @Override
    public Etudiant update(Long id, Etudiant etudiant) {
        Etudiant existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Étudiant introuvable"));

        // Règle : si matricule changé → vérifier unicité
        if (!existing.getMatricule().equals(etudiant.getMatricule())) {
            if (repo.findByMatricule(etudiant.getMatricule()).isPresent()) {
                throw new IllegalArgumentException("Matricule déjà utilisé");
            }
        }

        existing.setNom(etudiant.getNom());
        existing.setPrenom(etudiant.getPrenom());
        existing.setMatricule(etudiant.getMatricule());
        existing.setDateNaissance(etudiant.getDateNaissance());
        existing.setEmail(etudiant.getEmail());
        existing.setGroupe(etudiant.getGroupe());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Étudiant introuvable");
        }
        repo.deleteById(id);
    }

    @Override
    public Etudiant getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Étudiant introuvable"));
    }

    @Override
    public List<Etudiant> listAll() {
        return repo.findAll();
    }
    
    @Override
    public List<Etudiant> listByGroupe(Long groupId) {
        return repo.findByGroupe_Id(groupId);
    }
}
