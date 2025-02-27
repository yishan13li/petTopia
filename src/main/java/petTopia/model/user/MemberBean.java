package petTopia.model.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "member")
@Data
public class MemberBean {

	 @Id
	    private Integer id;  // 保持 id 為主鍵

	    @OneToOne
	    @JoinColumn(name = "id", referencedColumnName = "id")  
	    private UsersBean user;  // 用這個屬性來建立與 UsersBean 的關聯

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime birthdate;

    @Column(nullable = false)
    private Boolean gender = false;  // false = 男性, true = 女性

    private String address;

    @Lob
    @Column(name = "profile_photo")
    private byte[] profilePhoto;

    @Column(nullable = false)
    private Boolean status = false;  // 預設為未認證 (0)

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();

    // 這裡不需要額外的 user_id 外鍵欄位，因為 id 已經作為外鍵連結
    // 不需要額外的 @ManyToOne 或 @JoinColumn

    // getters 和 setters 會自動由 @Data 提供
}
