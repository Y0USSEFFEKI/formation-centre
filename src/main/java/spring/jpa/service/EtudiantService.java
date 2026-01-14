package spring.jpa.service;

import java.util.List;

import spring.jpa.entity.Etudiant;

public interface EtudiantService {
	Etudiant create(Etudiant student);
	Etudiant update(Long id, Etudiant student);
	void delete(Long id);
	Etudiant getById(Long id);
	List<Etudiant> listAll();
	List<Etudiant> listByGroupe(Long groupId);
}

