package spring.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import spring.jpa.entity.Inscription;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
	List<Inscription> findByEtudiant_Id(Long etudiantId);
	boolean existsByEtudiant_IdAndCours_Id(Long etudiantId, Long coursId);

	@Modifying
	@Transactional
	@Query("delete from Inscription i where i.etudiant.id = :etudiantId and i.cours.id = :coursId")
	int deleteByEtudiantIdAndCoursId(@Param("etudiantId") Long etudiantId, @Param("coursId") Long coursId);
}
