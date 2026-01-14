package spring.jpa.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import spring.jpa.entity.Seance;

public interface SeanceRepository extends JpaRepository<Seance, Long> {
	@Query("select s from Seance s where s.formateur.id = :formateurId and ((s.heureDebut < :end and s.heureFin > :start))")
	List<Seance> findConflictingSeancesForFormateur(Long formateurId, LocalDateTime start, LocalDateTime end);
	List<Seance> findByGroupe_Id(Long groupeId);
	List<Seance> findByFormateur_Id(Long formateurId);
	List<Seance> findByCours_IdInOrderByHeureDebutAsc(List<Long> coursIds);

	// Find seances that belong to a given groupe and whose cours is in the provided list,
	// ordered by heureDebut. This avoids in-memory filtering of other groupes.
	List<Seance> findByGroupe_IdAndCours_IdInOrderByHeureDebutAsc(Long groupeId, List<Long> coursIds);
}
