package petTopia.repository.vendor_admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.vendor_admin.VendorActivity;

public interface VendorActivityRepository extends JpaRepository<VendorActivity, Integer> {
	// 你可以根據需要增加查詢方法，例如：
	// List<VendorActivity> findByVendorId(Integer vendorId);

	List<VendorActivity> findByVendorId(Integer vendorId);
}
