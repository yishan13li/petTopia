package petTopia.service.vendor_admin;


import java.util.Optional;

import org.springframework.stereotype.Service;

import petTopia.model.vendor_admin.User;

@Service
public interface UserService {
	Optional<User> getUserByEmailAndPassword(String email, String password);

	User updateUser(User user);

	public boolean checkLogin(User users);
	public void deleteUserById(Integer userId);
}