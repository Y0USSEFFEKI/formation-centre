package spring.jpa.service;

import java.util.List;
import spring.jpa.entity.Formateur;

public interface FormateurService {
    Formateur create(Formateur f);
    Formateur update(Long id, Formateur f);
    void delete(Long id);
    Formateur getById(Long id);
    List<Formateur> listAll();
}
