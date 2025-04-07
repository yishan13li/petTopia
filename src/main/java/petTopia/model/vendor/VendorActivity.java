package petTopia.model.vendor;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vendor_activity")
//@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor

public class VendorActivity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "vendor_id", nullable = false)
	private Vendor vendor;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "start_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date startTime;

	@Column(name = "end_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date endTime;

	@Column(name = "is_registration_required", nullable = false)
	private boolean isRegistrationRequired = false;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "activity_type_id", nullable = false)
	private ActivityType activityType;

	@Column(name = "registration_date", updatable = false)

	private java.util.Date registrationDate = new Date();

	@Column(name = "number_visitor", nullable = false)
	private int numberVisitor = 0;

	@Column(name = "address", nullable = false)
	private String address;

	@JsonIgnore
	@OneToMany(mappedBy = "vendorActivity", cascade = CascadeType.ALL)
	private List<VendorActivityImages> vendorActivityImages;

	@JsonIgnore
	@BatchSize(size = 20)
	@OneToMany(mappedBy = "vendorActivity", cascade = CascadeType.ALL)
	private List<VendorActivityImages> images;

	@JsonIgnore
	public List<VendorActivityImages> getVendorActivityImages() {
		// TODO Auto-generated method stub
		return images;
	}

	@OneToOne(mappedBy = "vendorActivity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private ActivityPeopleNumber activityPeopleNumber;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public java.util.Date getStartTime() {
		return startTime;
	}

	public void setStartTime(java.util.Date startTime) {
		this.startTime = startTime;
	}

	public java.util.Date getEndTime() {
		return endTime;
	}

	public void setEndTime(java.util.Date endTime) {
		this.endTime = endTime;
	}

	public boolean getisRegistrationRequired() {
		return isRegistrationRequired;
	}

	public void setIsRegistrationRequired(boolean isRegistrationRequired) {
		this.isRegistrationRequired = isRegistrationRequired;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public java.util.Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(java.util.Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public int getNumberVisitor() {
		return numberVisitor;
	}

	public void setNumberVisitor(int numberVisitor) {
		this.numberVisitor = numberVisitor;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<VendorActivityImages> getImages() {
		return images;
	}

	public void setImages(List<VendorActivityImages> images) {
		this.images = images;
	}

	public ActivityPeopleNumber getActivityPeopleNumber() {
		return activityPeopleNumber;
	}

	public void setActivityPeopleNumber(ActivityPeopleNumber activityPeopleNumber) {
		this.activityPeopleNumber = activityPeopleNumber;
	}

	public void setVendorActivityImages(List<VendorActivityImages> vendorActivityImages) {
		this.vendorActivityImages = vendorActivityImages;
	}

}
