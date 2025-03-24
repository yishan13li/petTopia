package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import petTopia.model.vendor.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {

	public List<Vendor> findByVendorCategoryId(Integer vendorCategoryId);

	public List<Vendor> findByNameContaining(String name);

	public List<Vendor> findByDescriptionContaining(String description);

	/** 📌 批量更新店家狀態 */
	@Modifying
	@Query("UPDATE Vendor v SET v.status = :status WHERE v.id IN :vendorIds")
	void updateVendorStatusByIds(List<Integer> vendorIds, boolean status);

	/** 📌 更新所有店家的狀態 */
	@Modifying
	@Query("UPDATE Vendor v SET v.status = :status")
	void updateAllVendorStatus(boolean status);
	
    List<Vendor> findByStatus(boolean status);
    List<Vendor> findByVendorCategoryIdAndStatus(Integer categoryId, boolean status);
}
