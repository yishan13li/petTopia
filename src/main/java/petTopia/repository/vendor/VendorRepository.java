package petTopia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import petTopia.model.vendor.Vendor;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    // 根據用戶ID查找商家
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

    // 根據名稱模糊查詢
    List<Vendor> findByNameContaining(String name);

    // 根據店家類別ID查找商家
    List<Vendor> findByVendorCategoryId(Integer vendorCategoryId);

    // 根據描述模糊查詢
    List<Vendor> findByDescriptionContaining(String description);

    
    // 根據email查找商家，使用LEFT JOIN確保即使某些欄位為null也能查到
    @Query("SELECT v FROM Vendor v LEFT JOIN v.user u WHERE u.email = :email")
    Vendor findByEmail(@Param("email") String email);
}
