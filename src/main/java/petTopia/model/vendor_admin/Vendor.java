package petTopia.model.vendor_admin;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "vendor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Vendor {

	@Id
	@Column(name = "id")
	private Integer id;

	@OneToOne
	@JoinColumn(name = "id", referencedColumnName = "id")
	private User user;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Lob
	@Column(name = "logo_img", nullable = false)
	private byte[] logoImg;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "phone", nullable = false)
	private String phone;

	@Column(name = "contact_email", nullable = false)
	private String contactEmail;

	@Column(name = "contact_person", nullable = false)
	private String contactPerson;

	@Column(name = "taxid_number", nullable = false)
	private String taxidNumber;

	@Column(name = "status", nullable = false)
	private boolean status = false;

	@ManyToOne
	@JoinColumn(name = "vendor_category_id", referencedColumnName = "id")
	private VendorCategory vendorCategory;

	@Column(name = "registration_date", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date registrationDate = new Date();

	@Column(name = "updated_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate = new Date();

	@Column(name = "event_count", nullable = false)
	private int eventCount = 0;

	@Column(name = "total_rating", nullable = false)
	private float totalRating = 0;

	@Column(name = "review_count", nullable = false)
	private int reviewCount = 0;

	@Column(name = "avg_rating", nullable = false)
	private float avgRating = 0;

	@Column(name = "vendor_level", nullable = false)
	private String vendorLevel = "普通";

	@OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendorCertification> certifications;
    
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendorActivity> activities;
    
//    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<VendorReview> reviews;
//    
//    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Notification> notifications;
}