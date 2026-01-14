package spring.jpa.service;

import java.util.List;
import spring.jpa.entity.Seance;

public interface SeanceService {
    Seance create(Seance s);
    Seance update(Long id, Seance s);
    void delete(Long id);
    Seance getById(Long id);
    List<Seance> listAll();
}
