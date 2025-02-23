package petTopia.service.vendor_admin;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor_admin.User;
import petTopia.model.vendor_admin.Vendor;
import petTopia.repository.vendor_admin.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VendorServiceImpl vendorServiceImpl;

	@Override
	public Optional<User> getUserByEmailAndPassword(String email, String password) {
		return userRepository.findByEmailAndPassword(email, password);
	}

	@Override
	public User updateUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public boolean checkLogin(User users) {
		Optional<User> resultBean = userRepository.findByEmailAndPassword(users.getEmail(), users.getPassword());

		if (resultBean != null) {
			return true;
		}
		return false;
	}

	@Override
	public void deleteUserById(Integer userId) {

		// 刪除對應的 VendorDetail 資料
		Optional<Vendor> vendor = vendorServiceImpl.getVendorByUserId(userId);
		if (vendor.isPresent()) {
			vendorServiceImpl.deleteVendor(vendor.get()); // 刪除 VendorDetail
		}

		// 刪除 User 資料
		userRepository.deleteById(userId);
	}

}
