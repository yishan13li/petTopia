package petTopia.model.vendor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "email", "user_role" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") // 显式指定数据库的列名
	private Integer userId;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, unique = true)
	private String email;

//	@Enumerated(EnumType.STRING)
//    @Column(nullable = false ,name = "user_role")
//    private UserRole userRole;

	public User(String password, String email) {
		super();
		this.password = password;
		this.email = email;
	}
	
	
}


