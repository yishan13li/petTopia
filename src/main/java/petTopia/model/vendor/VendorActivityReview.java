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
@Entity
@Table(name="vendor_activity_review")
public class VendorActivityReview {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="vendor_id")
	private Integer vendorId;
	
	@Column(name="member_id")
	private Integer memberId;
	
	@Column(name="review_time")
	private Date reviewTime;
	
	@Column(name="review_content")
	private String reviewContent;
	
	@Column(name="vendor_activity_id")
	private Integer vendorActivityId;
	
}
