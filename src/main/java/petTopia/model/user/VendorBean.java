package petTopia.model.user;

import jakarta.persistence.*;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registration_date")
    private Date registrationDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate = new Date();

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
}
