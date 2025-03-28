package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.user.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    // 可以添加自定義的查詢方法
} 