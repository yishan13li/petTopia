package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import petTopia.model.user.UsersBean;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UsersBean, Integer> {
    // 基本的 CRUD 操作由 JpaRepository 提供
    
    // 可以添加自定義查詢方法
    UsersBean findByEmail(String email);
    
    UsersBean findByEmailAndPassword(String email, String password);
    
    UsersBean findByVerificationToken(String token);
    
    // 根據email和角色查找用戶
    UsersBean findByEmailAndUserRole(String email, UsersBean.UserRole userRole);
    
    // 根據角色查找用戶列表
    List<UsersBean> findByUserRole(UsersBean.UserRole userRole);
    @Query("SELECT u FROM UsersBean u WHERE u.email = :email ORDER BY u.id DESC")
    Optional<UsersBean> findFirstByEmailOrderByIdDesc(String email);
    public interface UserRepository extends JpaRepository<UsersBean, Integer> {

        // 修改查詢方法，需要同時用 email 和 role 來查詢
        Optional<UsersBean> findByEmailAndUserRole(String email, UsersBean.UserRole role);
        
        // 如果有這個方法，可能需要調整使用方式
        boolean existsByEmail(String email);
    }
}

