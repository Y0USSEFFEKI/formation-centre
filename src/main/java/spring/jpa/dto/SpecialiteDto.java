package spring.jpa.dto;

public class SpecialiteDto {
    private Long id;
    private String name;
    // getters/setters
	public void setId(Long id) {
		// TODO Auto-generated method stub
		this.id=id;
	}
	public Long getId() {
		return id;
	}
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name=name;
	}
	public String getName() {
		return name;
	}
}
