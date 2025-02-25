package petTopia.model.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Data
public class MemberDetailBean {
    
    @Id
    @Column(name = "id")
    private Integer id;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UsersBean user;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String phone;
    
    private LocalDateTime birthdate;
    
    @Column(nullable = false)
    private Boolean gender = false;
    
    private String address;
    
    @Lob
    @Column(name = "profile_photo")
    private byte[] profilePhoto;
    
    @Column(nullable = false)
    private Boolean status = false;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();
} 