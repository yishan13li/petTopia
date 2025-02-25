package petTopia.repository.vendor_admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import petTopia.model.vendor_admin.VendorActivityImages;

public interface VendorActivityImagesRepository extends JpaRepository<VendorActivityImages, Integer> {

//	@Query("SELECT vavi.id FROM VendorActivity vav JOIN vav.vendorActivityImages vavi WHERE vav.id = :vendorActivityId ORDER BY vavi.id ASC")
//	Optional<VendorActivityImages> findFirstByVendorActivityId(Integer vendorActivityId);
}
