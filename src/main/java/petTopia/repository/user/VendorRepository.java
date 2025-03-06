package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import petTopia.model.user.VendorBean;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<VendorBean, Integer> {
    // 根據用戶ID查找商家
    Optional<VendorBean> findByUserId(Integer userId);

    // 根據電話號碼查找商家
    Optional<VendorBean> findByPhone(String phone);

    // 根據名稱模糊查詢
    List<VendorBean> findByNameContaining(String name);

    @Query("SELECT v FROM VendorBean v JOIN v.user u WHERE u.email = ?1")
    VendorBean findByEmail(String email);
}
