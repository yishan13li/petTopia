package petTopia.service.vendor_admin;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.user.Users;
import petTopia.model.user.Vendor;
import petTopia.model.user.VendorCategory;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.user.VendorCategoryRepository;
import petTopia.repository.user.VendorRepository;
import petTopia.repository.vendor.VendorActivityRepository;



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

	public Optional<Users> getUserByEmailAndPassword(String email, String password) {
		Users user = userService.getUserByEmailAndPassword(email, password);
		return Optional.ofNullable(user);
	}

	public Optional<Vendor> getVendorProfile(String email, String password) {
		Optional<Users> user = getUserByEmailAndPassword(email, password);
		if (user.isPresent()) {
			return vendorRepository.findById(user.get().getId());
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
