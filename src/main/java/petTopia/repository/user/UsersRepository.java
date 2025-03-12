package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import petTopia.model.user.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    // 基本的 CRUD 操作由 JpaRepository 提供
    
    // 查詢任何類型的帳號（會員、商家、本地、Google）
    @Query("SELECT u FROM Users u WHERE LOWER(u.email) = LOWER(:email)")
    Users findByEmail(String email);
    
    Users findByEmailAndPassword(String email, String password);
    
    Users findByVerificationToken(String token);
    
    // 根據email和角色查找用戶
    Users findByEmailAndUserRole(String email, Users.UserRole userRole);
    
    // 根據角色查找用戶列表
    List<Users> findByUserRole(Users.UserRole userRole);

    @Query("SELECT u FROM Users u WHERE LOWER(u.email) = LOWER(:email) ORDER BY u.id DESC")
    Optional<Users> findFirstByEmailOrderByIdDesc(String email);
    
    // 檢查email是否存在
    boolean existsByEmail(String email);
    
    // 根據email和提供者查找用戶，返回列表以處理重複情況
    List<Users> findByEmailAndProvider(String email, Users.Provider provider);
    
    // 根據email、提供者和角色查找用戶
    List<Users> findByEmailAndProviderAndUserRole(String email, Users.Provider provider, Users.UserRole userRole);
    
    // 檢查用戶是否有商家帳號
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Users u WHERE LOWER(u.email) = LOWER(:email) AND u.userRole = 'VENDOR'")
    boolean hasVendorAccount(String email);
    
    // 根據會員郵箱查找對應的商家帳號
    @Query("SELECT u FROM Users u WHERE LOWER(u.email) = LOWER(:email) AND u.userRole = 'VENDOR'")
    Optional<Users> findVendorByEmail(String email);
    
    // 根據會員ID查找對應的商家帳號
    @Query("SELECT u FROM Users u WHERE u.email = (SELECT u2.email FROM Users u2 WHERE u2.id = :memberId) AND u.userRole = 'VENDOR'")
    Optional<Users> findVendorByMemberId(Integer memberId);
}

