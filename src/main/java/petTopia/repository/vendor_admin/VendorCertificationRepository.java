package petTopia.repository.vendor_admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import petTopia.model.vendor.VendorCertification;

public interface VendorCertificationRepository extends JpaRepository<VendorCertification, Integer> {

	List<VendorCertification> findByIdIn(List<Integer> certificationIds);

	@Query("SELECT vc.vendor.id FROM VendorCertification vc WHERE vc.certificationStatus = '已認證'")
    List<Integer> findCertifiedVendorIds();
}
