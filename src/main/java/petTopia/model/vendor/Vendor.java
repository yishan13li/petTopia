package petTopia.model.vendor;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.Data;
import petTopia.model.user.Users;

@Entity
@Table(name = "vendor")
@Data
public class Vendor {

    @Id
    @Column(name = "id")
    private Integer id;  // 不使用 @GeneratedValue，因為要與 user id 相同

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId  // 重要：使用 user 的 id 作為 vendor 的 id
    @JoinColumn(name = "id")
    private Users user;

    private String name;
    private String description;

    @Lob
    @Column(name = "logo_img")
    private byte[] logoImg;

    @Transient
    private String logoImgBase64;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_category_id", insertable = false, updatable = false)
    private VendorCategory vendorCategory;

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

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    private List<VendorImages> images;

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

    public void setVendorCategory(VendorCategory vendorCategory) {
        this.vendorCategory = vendorCategory;
        if (vendorCategory != null) {
            this.vendorCategoryId = vendorCategory.getId();
        }
    }
}
