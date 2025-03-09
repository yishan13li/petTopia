package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import petTopia.model.user.MemberBean;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberBean, Integer> {
    // 根据用户ID查找会员
    Optional<MemberBean> findByUserId(Integer userId);
    
    // 根据用户ID和状态查找会员
    Optional<MemberBean> findByUserIdAndStatus(Integer userId, Boolean status);
    
    // 使用LEFT JOIN查询，确保即使某些字段为null也能查到
    @Query("SELECT m FROM MemberBean m LEFT JOIN m.user u WHERE u.id = :userId")
    Optional<MemberBean> findByUserIdWithJoin(@Param("userId") Integer userId);
    
    // 检查会员是否存在
    boolean existsByUserId(Integer userId);
    
    // 根據手機號碼查找會員
    Optional<MemberBean> findByPhone(String phone);
    
    // 根據姓名模糊查詢
    List<MemberBean> findByNameContaining(String name);

    // 根據email查找會員，使用LEFT JOIN確保即使某些欄位為null也能查到
    @Query("SELECT m FROM MemberBean m LEFT JOIN m.user u WHERE u.email = :email")
    MemberBean findByEmail(@Param("email") String email);
} 