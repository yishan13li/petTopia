package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import petTopia.model.user.MemberBean;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberBean, Integer> {
    // 根據用戶ID查找會員
    Optional<MemberBean> findByUserId(Integer userId);
    
    // 根據手機號碼查找會員
    Optional<MemberBean> findByPhone(String phone);
    
    // 根據姓名模糊查詢
    List<MemberBean> findByNameContaining(String name);

    @Query("SELECT m FROM MemberBean m JOIN m.user u WHERE u.email = ?1")
    MemberBean findByEmail(String email);
} 