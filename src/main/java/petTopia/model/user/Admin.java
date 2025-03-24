package petTopia.model.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin")
@Data
public class Admin {
    
    @Id
    @Column(name = "id")
    private Integer id;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User users;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminRole role;
    
    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();
    
    public enum AdminRole {
        SA, ADMIN, EMPLOYEE
    }
}
