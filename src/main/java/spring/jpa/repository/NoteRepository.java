package spring.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.jpa.entity.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByCours_IdAndEtudiant_Groupe_Id(Long coursId, Long groupeId);
    List<Note> findByEtudiant_Id(Long etudiantId);
}
