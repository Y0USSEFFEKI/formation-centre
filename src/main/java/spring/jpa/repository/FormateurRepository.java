package spring.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.jpa.entity.Formateur;

public interface FormateurRepository extends JpaRepository<Formateur, Long> {
	Optional<Formateur> findByUser_Username(String username);
	Optional<Formateur> findByPrenomAndNom(String prenom, String nom);
}
