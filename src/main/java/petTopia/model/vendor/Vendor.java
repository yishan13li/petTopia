package petTopia.model.vendor;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

	@Column(name = "vendor_category_id")
	private Integer vendorCategoryId;

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
	
	/* 使用Transient防止被序列化，但要於Service層賦值 */
	@Transient
	private String logoImgBase64;
}
