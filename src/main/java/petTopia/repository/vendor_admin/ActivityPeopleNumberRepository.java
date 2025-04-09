package petTopia.repository.vendor_admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.ActivityPeopleNumber;

public interface ActivityPeopleNumberRepository extends JpaRepository<ActivityPeopleNumber, Integer> {

	 Optional<ActivityPeopleNumber> findByVendorActivity_Id(Integer activityId);

	 public ActivityPeopleNumber findByVendorActivityId(Integer activityId);
	 
}
