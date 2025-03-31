package petTopia.model.vendor;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "friendly_shop")
public class FriendlyShop {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "vendor_id")
	private Vendor vendor;

	@ManyToOne
	@JoinColumn(name = "vendor_category_id")
	private VendorCategory vendorCategory;

	@Column(name = "address")
	private String address;

	@Column(name = "longitude")
	private BigDecimal longitude;

	@Column(name = "latitude")
	private BigDecimal latitude;
}
