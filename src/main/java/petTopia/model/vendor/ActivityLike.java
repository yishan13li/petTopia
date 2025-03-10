package petTopia.model.vendor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import petTopia.model.user.MemberBean;

@Entity
@Table(name = "activity_like")
public class ActivityLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private MemberBean member;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "vendor_activity_id", nullable = false)
	private VendorActivity vendorActivity;
}
