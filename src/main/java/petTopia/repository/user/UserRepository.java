package petTopia.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.user.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
	 Optional<Users> findByEmail(String email);
}
