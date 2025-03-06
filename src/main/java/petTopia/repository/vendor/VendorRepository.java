package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
	
	public List<Vendor> findByVendorCategoryId(Integer vendorCategoryId);
	
	public List<Vendor> findByNameContaining(String name);
	
	public List<Vendor> findByDescriptionContaining(String description);

}
