package petTopia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorActivity;

public interface VendorActivityRepository extends JpaRepository<VendorActivity, Integer> {
	
}
