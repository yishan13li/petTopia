package petTopia.model.vendor;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petTopia.model.user.Member;

@Entity
@Table(name = "activity_registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRegistration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "vendor_activity_id", nullable = false)
	private VendorActivity vendorActivity;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "registration_time", nullable = false, updatable = false)
	private java.util.Date registrationTime = new Date();

	@Column(name = "status", nullable = false)
	private String status = "pending";
}
