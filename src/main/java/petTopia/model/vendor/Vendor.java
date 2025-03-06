package petTopia.model.vendor;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vendor")
public class Vendor {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

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
	private String taxIdNumber;

	@Column(name = "status")
	private boolean status;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vendor_category_id")
	private VendorCategory vendorCategory;

	@Column(name = "registration_date")
	private Date registrationDate;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "event_count")
	private Integer eventCount;

	@Column(name = "total_rating")
	private Double totalRating;

	@Column(name = "review_count")
	private Integer reviewCount;

	@Column(name = "avg_rating")
	private Double avgRating;

	@Column(name = "vendor_level")
	private String vendorLevel;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vendor", cascade = CascadeType.ALL)
	private List<VendorActivityReview> reviews;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vendor", cascade = CascadeType.ALL)
	private List<VendorImages> vendorImages;
	
	/* 使用Transient防止被序列化，用於Service層賦值 */
	@Transient
	private String logoImgBase64;
}
