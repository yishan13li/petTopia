package petTopia.model.vendor;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vendor_activity")
@Entity
public class VendorActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "vendor_id")
	private Vendor vendor;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "start_time")
	private Date startTime;

	@Column(name = "end_time")
	private Date endTime;

	@Column(name = "is_registration_required")
	private Boolean isRegistrationRequired;

	@ManyToOne
	@JoinColumn(name = "activity_type_id")
	private ActivityType activityType;

	@Column(name = "registration_date")
	private Date registrationDate;

	@Column(name = "number_visitor")
	private Integer numberVisitor;

	@Column(name = "address")
	private String address;

	@JsonIgnore
	@OneToMany(mappedBy = "vendorActivity", cascade = CascadeType.ALL)
	private List<VendorActivityImages> vendorActivityImages;
}
