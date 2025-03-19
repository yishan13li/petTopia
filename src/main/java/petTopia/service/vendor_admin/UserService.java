package petTopia.service.vendor_admin;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.user.Users;
import petTopia.repository.user.UsersRepository;


@Service
public class UserService {

	@Autowired
	private UsersRepository usersRepository;
//
//	@Autowired
//	private VendorService vendorService;
//
//	@Override
	public Users getUserByEmailAndPassword(String email, String password) {
		return usersRepository.findByEmailAndPassword(email, password);
	}
//
//	@Override
//	public User updateUser(User user) {
//		return userRepository.save(user);
//	}
//
//	@Override
//	public boolean checkLogin(User users) {
//		Optional<User> resultBean = userRepository.findByEmailAndPassword(users.getEmail(), users.getPassword());
//
//		if (resultBean != null) {
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public void deleteUserById(Integer userId) {
//
//		// 刪除對應的 VendorDetail 資料
//		Optional<Vendor> vendor = vendorService.getVendorByUserId(userId);
//		if (vendor.isPresent()) {
//			vendorService.deleteVendor(vendor.get()); // 刪除 VendorDetail
//		}
//
//		// 刪除 User 資料
//		userRepository.deleteById(userId);
//	}

}
