package petTopia.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="admin")
public class Admin {
	
    @Id
    @Column(name = "id")
    private int id; // 管理員ID，自動遞增主鍵
    
    @Column(name = "name", nullable = false)
    private String name; // 管理員姓名
    
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role; // 管理員角色，'SA', 'admin', 'employee'
    
    public enum Role {
        SA, admin, employee
    }
}
