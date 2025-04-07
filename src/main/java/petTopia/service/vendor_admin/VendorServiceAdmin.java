package petTopia.service.vendor_admin;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.user.User;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor_admin.VendorCertificationRepository;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;
import petTopia.service.user.UsersService;

@Service
public class VendorServiceAdmin {

	@Autowired
	private UsersService userService;

	@Autowired
	private VendorRepository vendorRepository; // Vendor 查詢

	@Autowired
	private VendorCategoryRepository vendorCategoryRepository; // 店家類別

	@Autowired
	private VendorActivityRepository vendorActivityRepository;
	
	@Autowired
    private VendorCertificationRepository vendorCertificationRepository;

    @Autowired
    private VendorCertificationTagRepository vendorCertificationTagRepository;

	public Optional<Vendor> getVendorById(Integer vendorId) {
		return vendorRepository.findById(vendorId);
	}

//	public Optional<User> getUserByEmailAndPassword(String email, String password) {
//		return userService.getUserByEmailAndPassword(email, password);
//	}

//	public Optional<Vendor> getVendorProfile(String email, String password) {
//		Optional<User> user = getUserByEmailAndPassword(email, password);
//		if (user.isPresent()) {  //&& user.get().getUserRole() == UserRole.vendor
//			return vendorRepository.findById(user.get().getId());
//		}
//		return Optional.empty();
//	}
	
	public Optional<Vendor> getVendorStatus(Integer vendorId) {
        return vendorRepository.findStatusById(vendorId);
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
	
	public List<String> getCertifiedVendorsSlogans() {
        // 获取所有认证的店铺ID
        List<Integer> certifiedVendorIds = vendorCertificationRepository.findCertifiedVendorIds();

        // 根据这些ID获取标语
        List<String> slogans = vendorCertificationTagRepository.findSlogansByVendorIds(certifiedVendorIds);

        return slogans;
    }
	
	public List<String> getSlogansByVendorId(Integer vendorId) {
	    // 获取已通过认证的标语
	    List<String> certifiedSlogans = vendorCertificationTagRepository.findCertifiedSlogansByVendorId(vendorId);
	    
	    return certifiedSlogans;
	}
}
