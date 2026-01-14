package spring.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Specialite {
	@Id @GeneratedValue Long id; 
	String name;
	
	public String getName() {
		return this.name;	
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public Long getId() {
		return this.id;
	}
}








