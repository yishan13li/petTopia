package petTopia.service.vendor_admin;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.User;
import petTopia.model.vendor.UserRole;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorRepository;

@Service
public class VendorServiceAdmin {

	@Autowired
	private UserService userService;

	@Autowired
	private VendorRepository vendorRepository; // Vendor 查詢

	@Autowired
	private VendorCategoryRepository vendorCategoryRepository; // 店家類別

	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	public Optional<Vendor> getVendorById(Integer vendorId) {
		return vendorRepository.findById(vendorId);
	}

	public Optional<User> getUserByEmailAndPassword(String email, String password) {
		return userService.getUserByEmailAndPassword(email, password);
	}

	public Optional<Vendor> getVendorProfile(String email, String password) {
		Optional<User> user = getUserByEmailAndPassword(email, password);
		if (user.isPresent()) {  //&& user.get().getUserRole() == UserRole.vendor
			return vendorRepository.findById(user.get().getUserId());
		}
		return Optional.empty();
	}

	public String getVendorLogoBase64(Vendor vendor) {
		if (vendor.getLogoImg() != null) {
			return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(vendor.getLogoImg());
		}
		return null;
	}

	public List<VendorCategory> getAllVendorCategories() {
		return vendorCategoryRepository.findAll();
	}

	public Vendor updateVendor(Vendor vendor) {
		return vendorRepository.save(vendor);
	}

	public void deleteVendor(Vendor vendor) {
		vendorRepository.delete(vendor);
	}

	public Optional<Vendor> getVendorByUserId(Integer userId) {
		return vendorRepository.findById(userId);
	}

	public int getActivityCountByVendor(Integer vendorId) {
		// 假设你有一个方法来获取该店家所有活动列表
		List<VendorActivity> activities = vendorActivityRepository.findByVendorId(vendorId);
		return activities.size();
	}

}
