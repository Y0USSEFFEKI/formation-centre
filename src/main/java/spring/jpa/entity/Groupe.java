package spring.jpa.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="groupes")
public class Groupe {

    @Id
    @GeneratedValue
    private Long id;

    private String nom;

    @ManyToOne
    private Specialite specialite;

    @OneToMany(mappedBy = "groupe")
    private List<Etudiant> etudiants;

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String name) {
        this.nom = name;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public List<Etudiant> getStudents() {
        return etudiants;
    }

    public void setStudents(List<Etudiant> students) {
        this.etudiants = students;
    }
}
