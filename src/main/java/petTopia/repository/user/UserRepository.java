package petTopia.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import petTopia.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	// 基本的 CRUD 操作由 JpaRepository 提供

	// 查詢任何類型的帳號（會員、商家、本地、Google）
	List<User> findByEmail(String email);

	User findByEmailAndPassword(String email, String password);

	User findByVerificationToken(String token);

	// 根據email和角色查找用戶
	User findByEmailAndUserRole(String email, User.UserRole userRole);

	// 根據角色查找用戶列表
	List<User> findByUserRole(User.UserRole userRole);

	@Query(value = "SELECT u.* FROM [users] u WHERE LOWER(u.email) = LOWER(:email) ORDER BY u.id DESC", nativeQuery = true)
	Optional<User> findFirstByEmailOrderByIdDesc(String email);

	// 檢查email是否存在
	boolean existsByEmail(String email);

	// 根據email和提供者查找用戶，返回列表以處理重複情況
	List<User> findByEmailAndProvider(String email, User.Provider provider);

	// 根據email、提供者和角色查找用戶
	List<User> findByEmailAndProviderAndUserRole(String email, User.Provider provider, User.UserRole userRole);

	// 檢查用戶是否有商家帳號
	@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM [users] u WHERE LOWER(u.email) = LOWER(:email) AND u.user_role = 'VENDOR'", nativeQuery = true)
	boolean hasVendorAccount(String email);

	// 根據會員郵箱查找對應的商家帳號
	@Query(value = "SELECT u.* FROM [users] u WHERE LOWER(u.email) = LOWER(:email) AND u.user_role = 'VENDOR'", nativeQuery = true)
	Optional<User> findVendorByEmail(String email);

	// 根據會員ID查找對應的商家帳號
	@Query(value = "SELECT u.* FROM [users] u WHERE u.email = (SELECT u2.email FROM [users] u2 WHERE u2.id = :memberId) AND u.user_role = 'VENDOR'", nativeQuery = true)
	Optional<User> findVendorByMemberId(Integer memberId);

	// 添加分頁和規格查詢支援
	Page<User> findAll(Specification<User> spec, Pageable pageable);
}
