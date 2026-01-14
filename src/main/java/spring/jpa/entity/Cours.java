package spring.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cours")
public class Cours {

    @Id
    @GeneratedValue
    private Long id;

    private String code;
    private String title;
    private int hours;

    @ManyToOne
    private Specialite specialty;
    
    @ManyToOne
    @JoinColumn(name = "formateur_id")
    private Formateur formateur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public Specialite getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialite specialty) {
        this.specialty = specialty;
    }
    
    public Formateur getFormateur() {
        return formateur;
    }

    public void setFormateur(Formateur formateur) {
        this.formateur = formateur;
    }
}
