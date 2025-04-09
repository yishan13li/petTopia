package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;

public interface VendorActivityRepository extends JpaRepository<VendorActivity, Integer> {
	// 你可以根據需要增加查詢方法，例如：
	// List<VendorActivity> findByVendorId(Integer vendorId);
	@Query("SELECT va FROM VendorActivity va " + "LEFT JOIN FETCH va.vendor " + "LEFT JOIN FETCH va.activityType "
			+ "LEFT JOIN FETCH va.images " + "WHERE va.vendor.id = :vendorId")
	List<VendorActivity> findByVendorId(Integer vendorId);

	int countByVendor(Vendor vendor);

	public List<VendorActivity> findByActivityType(ActivityType activityType);

	public List<VendorActivity> findByNameContaining(String name);

	public List<VendorActivity> findByDescriptionContaining(String description);

	public List<VendorActivity> findByAddressContaining(String adress);

	// 統計總活動數
	@Query("SELECT COUNT(va) FROM VendorActivity va")
	long countTotalActivities();

}
