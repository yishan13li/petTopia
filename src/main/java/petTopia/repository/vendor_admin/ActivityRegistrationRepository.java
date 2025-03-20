package petTopia.repository.vendor_admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.ActivityRegistration;

public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Integer> {

	List<ActivityRegistration> findByVendorActivityId(Integer vendorActivityId);
	
	public Optional<ActivityRegistration> findByMemberId(Integer memberId);

}
