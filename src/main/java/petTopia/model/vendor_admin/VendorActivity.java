package petTopia.model.vendor_admin;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "vendor_activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VendorActivity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "vendor_id", nullable = false)
	private Vendor vendor;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "start_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;

	@Column(name = "end_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;

	@Column(name = "is_registration_required", nullable = false)
	private boolean isRegistrationRequired = false;

	@ManyToOne
	@JoinColumn(name = "activity_type_id", nullable = false)
	private ActivityType activityType;

	@Column(name = "registration_date", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date registrationDate = new Date();

	@Column(name = "number_visitor", nullable = false)
	private int numberVisitor = 0;

	@Column(name = "address", nullable = false)
	private String address;

	@OneToMany(mappedBy = "vendorActivity", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<VendorActivityImages> images;

	public List<VendorActivityImages> getVendorActivityImages() {
		// TODO Auto-generated method stub
		return images;
	}
}