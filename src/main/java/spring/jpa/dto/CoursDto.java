package spring.jpa.dto;

public class CoursDto {
    private Long id;
    private String code;
    private String title;
    private Long specialiteId;
    private String specialiteName;
    // getters/setters
    public Long getId() {
    	return id;
    }
	public void setId(Long id) {
		// TODO Auto-generated method stub
		this.id=id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		// TODO Auto-generated method stub
		this.code=code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		// TODO Auto-generated method stub
		this.title=title;
	}
	public Long getSpecialiteId() {
		return specialiteId;
	}
	public void setSpecialiteId(Long id) {
		// TODO Auto-generated method stub
		this.specialiteId=id;
	}
	public String getSpecialiteName() {
		return specialiteName;
	}
	public void setSpecialiteName(String specialityName) {
		// TODO Auto-generated method stub
		this.specialiteName=specialityName;
	}
}
