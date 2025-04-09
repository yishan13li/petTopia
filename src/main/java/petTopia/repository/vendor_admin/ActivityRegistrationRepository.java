package petTopia.repository.vendor_admin;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import petTopia.dto.vendor_admin.TopActivityDTO;
import petTopia.model.user.Member;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.model.vendor.VendorActivity;

public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Integer> {

	List<ActivityRegistration> findByVendorActivityId(Integer vendorActivityId);

	public Optional<ActivityRegistration> findByMemberIdAndVendorActivityId(Integer memberId, Integer vendorActivityId);

	public ActivityRegistration findByMemberAndVendorActivity(Member member, VendorActivity activity);

	List<ActivityRegistration> findByVendorActivityIdAndStatus(Integer vendorActivityId, String status);

	public Integer countByVendorActivityId(Integer vendorActivityId);

	public List<ActivityRegistration> findAllByMemberId(Integer memberId);

	@Transactional
	void deleteByVendorActivityId(Integer activityId);

	@Query(value = """
		    SELECT new petTopia.dto.vendor_admin.TopActivityDTO(va.id, va.name, COUNT(ar.id),MAX(va.description))
		    FROM ActivityRegistration ar
		    JOIN ar.vendorActivity va
		    GROUP BY va.id, va.name
		    ORDER BY COUNT(ar.id) DESC
		    """)
		List<TopActivityDTO> findTop5Activities(Pageable pageable);


}
