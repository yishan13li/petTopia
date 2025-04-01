package petTopia.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import petTopia.model.user.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    
    Optional<Member> findById(int id);  // 根據 id 查找 Member 資料

    // 根据用户ID查找会员
    Optional<Member> findByUserId(Integer userId);
    
    // 根据用户ID和状态查找会员
    Optional<Member> findByUserIdAndStatus(Integer userId, Boolean status);
    
    // 使用LEFT JOIN查询，确保即使某些字段为null也能查到
    @Query("SELECT m FROM Member m LEFT JOIN m.user u WHERE u.id = :userId")
    Optional<Member> findByUserIdWithJoin(@Param("userId") Integer userId);
    
    // 检查会员是否存在
    boolean existsByUserId(Integer userId);
    
    // 根據手機號碼查找會員
    Optional<Member> findByPhone(String phone);
    
    // 根據姓名模糊查詢
    List<Member> findByNameContaining(String name);

    // 根據email查找會員，使用LEFT JOIN確保即使某些欄位為null也能查到
    @Query("SELECT m FROM Member m LEFT JOIN m.user u WHERE u.email = :email")
    Member findByEmail(@Param("email") String email);

    // 根據會員ID刪除會員記錄
    @Modifying
    @Query("DELETE FROM Member m WHERE m.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
    
    List<Member> findAllById(Iterable<Integer> memberIds);
    
    // 統計總會員數
    @Query("SELECT COUNT(m) FROM Member m")
    long countTotalMembers();
    
} 

