package petTopia.model.vendor;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vendor_activity_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorActivityReview {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "vendor_id", nullable = false)
	private Vendor vendor;

	@Column(name = "member_id")
	private Integer memberId;

//    @ManyToOne
//    @JoinColumn(name = "member_id", nullable = false)
//    private MemberBean member;

	@Column(name = "review_time", nullable = false)
	private java.util.Date reviewTime;

	@Column(name = "review_content", nullable = false, length = 255)
	private String reviewContent;

	@ManyToOne
	@JoinColumn(name = "vendor_activity_id", nullable = false)
	private VendorActivity vendorActivity;

}
