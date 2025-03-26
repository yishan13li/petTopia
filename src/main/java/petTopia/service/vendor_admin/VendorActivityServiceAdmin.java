package petTopia.service.vendor_admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;

@Service
public class VendorActivityServiceAdmin {

	@Autowired
	private VendorActivityRepository vendorActivityRepository;
	
	@Autowired
	private VendorActivityImagesRepository vendorActivityImagesRepository;

	public VendorActivity saveVendorActivity(VendorActivity vendorActivity) {
		return vendorActivityRepository.save(vendorActivity);
	}

	public List<VendorActivity> getAllVendorActivities() {
		return vendorActivityRepository.findAll();
	}

	public List<VendorActivity> getVendorActivityByVendorId(Integer vendorId) {
		return vendorActivityRepository.findByVendorId(vendorId);
	}

	public void deleteVendorActivity(Integer id) {
		vendorActivityRepository.deleteById(id);

	}

	public void addActivity(VendorActivity activity) {
//        activity.setRegistrationDate(LocalDateTime.now()); // 設定當前時間為註冊時間
		vendorActivityRepository.save(activity);
	}

//	public Optional<Integer> getFirstImageIdByVendorActivityId(Integer vendorActivityId) {
//		return vendorActivityImagesRepository.findFirstByVendorActivityId(vendorActivityId)
//				.map(VendorActivityImages::getId);
//	}
	
	public Optional<VendorActivity> getVendorActivityById(Integer id) {
		return vendorActivityRepository.findById(id);
	}
	
}
