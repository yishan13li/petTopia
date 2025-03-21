package petTopia.repository.vendor_admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityImages;

public interface VendorActivityImagesRepository extends JpaRepository<VendorActivityImages, Integer> {

//	@Query("SELECT vavi.id FROM VendorActivity vav JOIN vav.vendorActivityImages vavi WHERE vav.id = :vendorActivityId ORDER BY vavi.id ASC")
//	Optional<VendorActivityImages> findFirstByVendorActivityId(Integer vendorActivityId);
	
	void deleteAllByIdIn(List<Integer> ids);
	
	public List<VendorActivityImages> findByVendorActivity(VendorActivity vendorActivity);
}
