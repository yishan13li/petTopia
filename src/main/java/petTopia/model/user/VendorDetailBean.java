package petTopia.model.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_detail")
@Data
public class VendorDetailBean {
    
    @Id
    @Column(name = "vendor_id")
    private Integer vendorId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "vendor_id")
    private UsersBean user;
    
    @Column(name = "vendor_name", nullable = false)
    private String vendorName;
    
    @Column(name = "vendor_description", nullable = false)
    private String vendorDescription;
    
    @Lob
    @Column(name = "vendor_logo_img", nullable = false)
    private byte[] vendorLogoImg;
    
    @Column(name = "vendor_address", nullable = false)
    private String vendorAddress;
    
    @Column(name = "vendor_phone", nullable = false)
    private String vendorPhone;
    
    @Column(name = "contact_email", nullable = false)
    private String contactEmail;
    
    @Column(name = "vendor_taxid_number", nullable = false)
    private String vendorTaxidNumber;
    
    private Boolean status = false;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private VendorCategoryBean category;
    
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
} 