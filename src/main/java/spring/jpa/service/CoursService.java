package spring.jpa.service;

import java.util.List;
import spring.jpa.entity.Cours;

public interface CoursService {
    Cours create(Cours c);
    Cours update(Long id, Cours c);
    void delete(Long id);
    Cours getById(Long id);
    List<Cours> listAll();
}
