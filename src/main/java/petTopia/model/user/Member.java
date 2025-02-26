package petTopia.model.user;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "member")
@Entity
public class Member {
	
	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "phone") 
	private String phone;
	
	@Column(name = "birthdate")
	private Date birthdate;
	
	@Column(name = "gender")
	private boolean gender;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "profile_photo")
	private byte[] profilePhoto;
	
	@Column(name = "status")
	private boolean status;
	
	@Column(name = "updated_date")
	private Date updatedDate;
}
