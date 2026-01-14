package spring.jpa.dto;

import java.util.Set;

public class AuthResponse {
    private String token;
    private Set<String> roles;
    private String fullname;
    // getters/setters
    public String getToken() {
        return token;
    }

	public void setToken(String token) {
		// TODO Auto-generated method stub
		this.token=token;
	}

    public Set<String> getRoles() {
        return roles;
    }

	public void setRoles(Set<String> roles) {
		// TODO Auto-generated method stub
		this.roles=roles;
	}

    public String getFullname() {
        return fullname;
    }

	public void setFullname(String fullname) {
		// TODO Auto-generated method stub
		this.fullname=fullname;
	}
}
