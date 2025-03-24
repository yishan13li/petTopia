package petTopia.dto.vendor_admin;

import java.time.LocalDate;


public class MemberDTO {

	private Integer id;

	private String name;

	private String phone;

	private LocalDate birthdate;

	private Boolean gender;

	public MemberDTO(Integer id, String name, String phone, LocalDate birthdate, Boolean gender) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.birthdate = birthdate;
		this.gender = gender;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDate getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
	}

	public Boolean getGender() {
		return gender;
	}

	public void setGender(Boolean gender) {
		this.gender = gender;
	}

}
