package petTopia.model.vendor;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petTopia.model.user.User;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vendor")
@AllArgsConstructor


public class Vendor {

	@Id
	@Column(name = "id")
	@MapsId
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "id")
	@JsonIgnore
	private User user;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@JsonIgnore
	@Column(name = "logo_img")
	private byte[] logoImg;

	@Column(name = "address")
	private String address;

	@Column(name = "phone")
	private String phone;

	@Column(name = "contact_email")
	private String contactEmail;

	@Column(name = "contact_person")
	private String contactPerson;

	@Column(name = "taxid_number")
	private String taxidNumber;

	@Column(name = "status", nullable = false)
	private boolean status = false;

	@ManyToOne
	@JoinColumn(name = "vendor_category_id")
	private VendorCategory vendorCategory;

	@Column(name = "registration_date", updatable = false)
	private java.util.Date registrationDate = new Date();

	@Column(name = "updated_date")
	private java.util.Date updatedDate = new Date();

	@Column(name = "event_count")
	private int eventCount = 0;

	@Column(name = "total_rating")
	private float totalRating = 0;

	@Column(name = "review_count")
	private int reviewCount = 0;

	@Column(name = "vendor_level", nullable = false)
	private String vendorLevel = "普通";
	
	@Column(name = "avg_rating_environment")
	private float avgRatingEnvironment = 0;
	
	@Column(name = "avg_rating_price")
	private float avgRatingPrice = 0;
	
	@Column(name = "avg_rating_service")
	private float avgRatinService = 0;

	@JsonIgnore
	@OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalendarEvent> calendarEvents;
	
	@JsonIgnore
	@OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<VendorCertification> certifications;

	@JsonIgnore
	@OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<VendorActivity> activities;

//    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<VendorReview> reviews;
//    
//    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Notification> notifications;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vendor", cascade = CascadeType.ALL)
	private List<VendorActivityReview> reviews;

//	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vendor", cascade = CascadeType.ALL)
	private List<VendorImages> vendorImages;

	

	/* 使用Transient防止被序列化，用於Service層賦值 */
	@Transient
	private String logoImgBase64;

}
