package petTopia.model.vendor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="vendor_activity_images")
public class VendorActivityImages {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="vendor_activity_id")
	private Integer vendorActivityId;
	
	@Column(name="image")
	private byte[] image;
	
	/* 使用Transient防止被序列化，用於Service層賦值 */
	@Transient
	private String imageBase64;
}
