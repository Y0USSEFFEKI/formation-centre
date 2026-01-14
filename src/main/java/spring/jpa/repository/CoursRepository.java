package spring.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.jpa.entity.Cours;

public interface CoursRepository extends JpaRepository<Cours, Long> {
	List<Cours> findBySpecialty_Name(String specialtyName);
	List<Cours> findBySpecialty_Id(Long specialiteId);
	List<Cours> findByFormateur_Id(Long formateurId);
}
