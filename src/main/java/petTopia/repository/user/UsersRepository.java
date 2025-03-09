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
    
    // 可以添加自定義查詢方法
    Users findByEmail(String email);
    
    Users findByEmailAndPassword(String email, String password);
    
    Users findByVerificationToken(String token);
    
    // 根據email和角色查找用戶
    Users findByEmailAndUserRole(String email, Users.UserRole userRole);
    
    // 根據角色查找用戶列表
    List<Users> findByUserRole(Users.UserRole userRole);
    @Query("SELECT u FROM Users u WHERE u.email = :email ORDER BY u.id DESC")
    Optional<Users> findFirstByEmailOrderByIdDesc(String email);
    public interface UserRepository extends JpaRepository<Users, Integer> {

        // 修改查詢方法，需要同時用 email 和 role 來查詢
        Optional<Users> findByEmailAndUserRole(String email, Users.UserRole role);
        
        // 如果有這個方法，可能需要調整使用方式
        boolean existsByEmail(String email);
    }
}

