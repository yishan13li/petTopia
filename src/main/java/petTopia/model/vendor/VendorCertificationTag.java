package petTopia.model.vendor;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Entity
@Table(name = "vendor_certification_tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class VendorCertificationTag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "vendor_id", nullable = false)
	private Vendor vendor;

	@ManyToOne
	@JoinColumn(name = "certification_id", nullable = false)
	private VendorCertification certification;

	@ManyToOne
	@JoinColumn(name = "tag_id", nullable = false)
	private CertificationTag tag;

	@Column(name = "meets_standard", nullable = false)
	private boolean meetsStandard = false;

}
