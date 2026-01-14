package spring.jpa.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String prenom;
    private String nom;
    private String email;
    private String role; // "ETUDIANT" or "FORMATEUR"
    // getters/setters
	public String getUsername() {
		// TODO Auto-generated method stub
		return username;
	}
    public void setUsername(String username) {
        this.username = username;
    }
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}
    public void setPassword(String password) {
        this.password = password;
    }
	public String getPrenom() {
		// TODO Auto-generated method stub
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
	public String getRole() {
		// TODO Auto-generated method stub
		return role;
	}
    public void setRole(String role) {
        this.role = role;
    }
	public String getEmail() {
		// TODO Auto-generated method stub
		return email;
	}
    public void setEmail(String email) {
        this.email = email;
    }
}
