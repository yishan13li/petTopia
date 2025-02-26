package petTopia.model.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "member")
@Data
public class MemberBean {
    @Id
    private Integer id;  // 對應 users 表的 id

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime birthdate;

    @Column(nullable = false)
    private Boolean gender = false;  // false=男性, true=女性

    private String address;

    @Lob
    @Column(name = "profile_photo")
    private byte[] profilePhoto;

    @Column(nullable = false)
    private Boolean status = false;  // 預設為未認證 (0)

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();

    // 添加與 Users 表的關聯
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UsersBean user;

    // getters and setters
    // ... 
} 