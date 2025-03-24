package petTopia.repository.vendor_admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.user.Member;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.model.vendor.VendorActivity;

public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Integer> {

	List<ActivityRegistration> findByVendorActivityId(Integer vendorActivityId);
	
	public Optional<ActivityRegistration> findByMemberId(Integer memberId);

	public ActivityRegistration findByMemberAndVendorActivity(Member member,VendorActivity activity);
	
	List<ActivityRegistration> findByVendorActivityIdAndStatus(Integer vendorActivityId, String status);
	
	public Integer countByVendorActivityId(Integer vendorActivityId);
}
