package petTopia.model.vendor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "calendar_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer eventId;

	
	@ManyToOne
	@JoinColumn(name = "vendor_id", nullable = false)
	private Vendor vendor;

	@Column(name = "event_title", nullable = false, length = 255)
	private String eventTitle;

	@Column(name = "start_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date startTime;

	@Column(name = "end_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date endTime;

	@Column(name = "color")
	private String color;

	
	@ManyToOne
	@JoinColumn(name = "vendor_activity_id", referencedColumnName = "id", nullable = false)
	private VendorActivity vendorActivity;

	@Column(name = "created_at", columnDefinition = "DATETIME DEFAULT GETDATE()", updatable = false)
	private java.util.Date createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT GETDATE()")
	private java.util.Date updatedAt;
}
