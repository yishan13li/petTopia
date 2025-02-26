package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import petTopia.model.user.UsersBean;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UsersBean, Integer> {
    UsersBean findByEmail(String email);
    
    @Query("SELECT u FROM UsersBean u WHERE u.email = :email ORDER BY u.id DESC")
    Optional<UsersBean> findFirstByEmailOrderByIdDesc(String email);
} 