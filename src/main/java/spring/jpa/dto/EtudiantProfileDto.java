package spring.jpa.dto;

public class EtudiantProfileDto {
    private Long id;
    private String prenom;
    private String nom;
    private String email;
    private Long groupeId;
    private String groupeNom;
    // getters/setters
	public void setId(Long id) {
		// TODO Auto-generated method stub
		this.id=id;
	}
	public void setPrenom(String prenom) {
		// TODO Auto-generated method stub
		this.prenom=prenom;
	}
	public void setNom(String nom) {
		// TODO Auto-generated method stub
		this.nom=nom;
	}
	public void setEmail(String email) {
		// TODO Auto-generated method stub
		this.email=email;
	}
	public void setGroupeId(Long groupeId) {
		// TODO Auto-generated method stub
		this.groupeId=groupeId;
	}
	public void setGroupeNom(String groupeNom) {
		// TODO Auto-generated method stub
		this.groupeNom=groupeNom;
	}
	public String getPrenom() {
		// TODO Auto-generated method stub
		return prenom;
	}
	public String getNom() {
		// TODO Auto-generated method stub
		return nom;
	}
	public String getEmail() {
		// TODO Auto-generated method stub
		return email;
	}
}
