package spring.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.jpa.entity.Groupe;

public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    // Tu peux ajouter des méthodes personnalisées ici, si besoin
    boolean existsByNom(String nom);
    List<Groupe> findBySpecialite_Id(Long specialiteId);
}
