package spring.jpa.service;

import java.util.List;
import spring.jpa.entity.Specialite;

public interface SpecialiteService {
    Specialite create(Specialite s);
    Specialite update(Long id, Specialite s);
    void delete(Long id);
    Specialite getById(Long id);
    List<Specialite> listAll();
}
