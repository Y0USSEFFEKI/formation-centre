package spring.jpa.dto;

public class GroupeDto {
    private Long id;
    private String nom;
    private Long specialiteId;
    private String specialiteName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getSpecialiteId() {
        return specialiteId;
    }

    public void setSpecialiteId(Long specialiteId) {
        this.specialiteId = specialiteId;
    }

    public String getSpecialiteName() {
        return specialiteName;
    }

    public void setSpecialiteName(String specialiteName) {
        this.specialiteName = specialiteName;
    }
}
