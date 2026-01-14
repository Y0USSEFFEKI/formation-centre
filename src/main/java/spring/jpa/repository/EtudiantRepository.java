package spring.jpa.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.jpa.entity.Etudiant;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
	Optional<Etudiant> findByMatricule(String matricule);
	List<Etudiant> findByGroupe_Id(Long groupId);
	Optional<Etudiant> findByUser_Username(String username);
}
