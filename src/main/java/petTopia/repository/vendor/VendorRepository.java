package petTopia.repository.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

	Optional<Vendor> findByUserId(Integer userId);

	// 根據用戶ID和狀態查找商家
	Optional<Vendor> findByUserIdAndStatus(Integer userId, Boolean status);

	// 使用LEFT JOIN查詢，確保即使某些欄位為null也能查到
	@Query("SELECT v FROM Vendor v LEFT JOIN v.user u WHERE u.id = :userId")
	Optional<Vendor> findByUserIdWithJoin(@Param("userId") Integer userId);

	// 檢查商家是否存在
	boolean existsByUserId(Integer userId);

	// 根據電話號碼查找商家
	Optional<Vendor> findByPhone(String phone);

	// 根據email查找商家，使用LEFT JOIN確保即使某些欄位為null也能查到
	@Query("SELECT v FROM Vendor v LEFT JOIN v.user u WHERE u.email = :email")
	Vendor findByEmail(@Param("email") String email);

	List<Vendor> findByVendorCategoryIdAndIdNot(Integer categoryId, Integer vendorId);

	List<Vendor> findAllByIdNot(Integer vendorId);

	Optional<Vendor> findStatusById(Integer vendorId);
	// 統計總店家數
	@Query("SELECT COUNT(v) FROM Vendor v")
	long countTotalVendors();

}
