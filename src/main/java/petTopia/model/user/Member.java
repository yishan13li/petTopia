package petTopia.model.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "member")
@Data
public class Member {

	 @Id
	    private Integer id;  // 與 user id 相同

	    @OneToOne
	    @JoinColumn(name = "id", referencedColumnName = "id")  
	    private Users user;  // 用這個屬性來建立與 Users 的關聯

    @Column(name = "name", nullable = true)
    private String name;

    @Column(nullable = true)
    private String phone;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(nullable = false)
    private Boolean gender = false;  // false = 男性, true = 女性

    private String address;

    @Lob
    @Column(name = "profile_photo")
    private byte[] profilePhoto;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean status = false;  // 預設為未認證 (0)

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // 這裡不需要額外的 user_id 外鍵欄位，因為 id 已經作為外鍵連結
    // 不需要額外的 @ManyToOne 或 @JoinColumn

    // getters 和 setters 會自動由 @Data 提供
}
