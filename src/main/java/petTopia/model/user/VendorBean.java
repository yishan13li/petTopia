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
    private Integer id; // 保持 id 為主鍵

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private UsersBean user; // 用這個屬性來建立與 UsersBean 的關聯

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
    private int vendorCategoryId;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();

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
