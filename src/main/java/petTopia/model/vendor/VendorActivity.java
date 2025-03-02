package petTopia.model.vendor;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	@Column(name = "vendor_id")
	private Integer vendorId;

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

	@Column(name = "activity_type_id")
	private Integer activityTypeId;

	@Column(name = "registration_date")
	private Date RegistrationDate;

	@Column(name = "number_visitor")
	private Integer numberVisitor;

	@Column(name = "address")
	private String address;
}
