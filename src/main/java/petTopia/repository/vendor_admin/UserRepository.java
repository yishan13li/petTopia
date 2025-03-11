package petTopia.repository.vendor_admin;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.user.UsersBean;
import petTopia.model.vendor.User;

@Repository
public interface UserRepository extends JpaRepository<UsersBean, Integer> {
    Optional<UsersBean> findByEmailAndPassword(String email,String password);
}