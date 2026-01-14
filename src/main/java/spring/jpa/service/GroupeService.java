package spring.jpa.service;

import java.util.List;
import spring.jpa.entity.Groupe;

public interface GroupeService {
    Groupe create(Groupe g);
    Groupe update(Long id, Groupe g);
    void delete(Long id);
    Groupe findById(Long id);
    List<Groupe> listAll();
    List<Groupe> listBySpecialite(Long specialiteId);
}
