package spring.jpa.service;

import java.util.List;
import spring.jpa.entity.Inscription;

public interface InscriptionService {
    Inscription create(Inscription i);
    Inscription update(Long id, Inscription i);
    void delete(Long id);
    Inscription getById(Long id);
    List<Inscription> listAll();
}
