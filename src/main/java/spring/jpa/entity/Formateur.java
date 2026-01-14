package spring.jpa.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "formateurs")
public class Formateur {

    @Id
    @GeneratedValue
    private Long id;

    private String prenom;
    private String nom;
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
        name = "formateur_specialite",
        joinColumns = @JoinColumn(name = "formateur_id"),
        inverseJoinColumns = @JoinColumn(name = "specialite_id")
    )
    private Set<Specialite> specialties = new HashSet<>();
    
    @OneToMany(mappedBy = "formateur")
    private Set<Cours> cours = new HashSet<>();

    public Set<Cours> getCours() {
        return cours;
    }

    public void setCours(Set<Cours> cours) {
        this.cours = cours;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Specialite> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<Specialite> specialties) {
        this.specialties = specialties;
    }
}
