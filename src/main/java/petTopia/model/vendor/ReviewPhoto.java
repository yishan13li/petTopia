package petTopia.model.vendor;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review_photo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPhoto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "vendor_review_id", nullable = false)
	private VendorReview vendorReview;

	@JsonIgnore
	@Lob
	@Column(name = "photo", nullable = false)
	private byte[] photo;

	/* 使用Transient防止被序列化，用於Service層賦值 */
	@Transient
	private String photoBase64;
}

