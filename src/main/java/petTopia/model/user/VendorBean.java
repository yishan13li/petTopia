package petTopia.model.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "vendor")
@Data
public class VendorBean {

    @Id
    @Column(name = "id")
    private Integer id;  // 不使用 @GeneratedValue，因為要與 user id 相同

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId  // 重要：使用 user 的 id 作為 vendor 的 id
    @JoinColumn(name = "id")
    private UsersBean user;

    private String name;
    private String description;

    @Lob
    @Column(name = "logo_img")
    private byte[] logoImg;

    private String address;
    private String phone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "taxid_number")
    private String taxidNumber;

    private Boolean status = false;

    @Column(name = "vendor_category_id")
    private Integer vendorCategoryId = 1;  // 預設分類

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "event_count")
    private int eventCount;

    @Column(name = "total_rating")
    private float totalRating;

    @Column(name = "review_count")
    private int reviewCount;

    @Column(name = "avg_rating")
    private float avgRating;

    @Column(name = "vendor_level")
    private String vendorLevel;

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
