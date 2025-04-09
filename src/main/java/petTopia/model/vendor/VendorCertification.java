package petTopia.model.vendor;

import java.util.Date;

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
@Table(name = "vendor_certification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class VendorCertification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "vendor_id", nullable = false)
	private Vendor vendor;

	@Column(name = "certification_status", nullable = false, columnDefinition = "NVARCHAR(50) DEFAULT '申請中'")
	private String certificationStatus;

	@Column(name = "reason", length = 1000)
	private String reason;

	@Column(name = "request_date", updatable = false)
	private java.util.Date requestDate = new Date();

	@Column(name = "approved_date")
	private java.util.Date approvedDate;

}