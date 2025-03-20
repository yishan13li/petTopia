package petTopia.model.vendor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vendor_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorReview {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "id")
	private Integer id;

	@Column(name = "vendor_id", nullable = false)
	private Integer vendorId;

//  @OneToOne(cascade = CascadeType.ALL)
//  @JoinColumn(name = "member_id", referencedColumnName = "id")
//	private MemberBean member;

	@Column(name = "member_id", nullable = false)
	private Integer memberId;

	@Column(name = "review_time", nullable = false)
	private java.util.Date reviewTime;

	@Column(name = "review_content")
	private String reviewContent;

	@Column(name = "rating_environment")
	private Integer ratingEnvironment;

	@Column(name = "rating_price")
	private Integer ratingPrice;

	@Column(name = "rating_service")
	private Integer ratingService;

//	@JsonManagedReference
	@OneToMany(mappedBy = "vendorReview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ReviewPhoto> reviewPhotos;
}
