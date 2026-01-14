package spring.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.jpa.entity.Specialite;

public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {
    boolean existsByName(String name);
}
