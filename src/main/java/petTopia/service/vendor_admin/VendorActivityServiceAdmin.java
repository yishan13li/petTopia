package petTopia.service.vendor_admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petTopia.dto.vendor_admin.TopActivityDTO;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.CalendarEventRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Service
public class VendorActivityServiceAdmin {

	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	@Autowired
	private VendorActivityImagesRepository vendorActivityImagesRepository;

	@Autowired
	private CalendarEventRepository calendarEventRepository;

	@Autowired
	private ActivityRegistrationRepository activityRegistrationRepository;

	public VendorActivity saveVendorActivity(VendorActivity vendorActivity) {
		return vendorActivityRepository.save(vendorActivity);
	}

	public List<VendorActivity> getAllVendorActivities() {
		return vendorActivityRepository.findAll();
	}

	public List<VendorActivity> getVendorActivityByVendorId(Integer vendorId) {
		return vendorActivityRepository.findByVendorId(vendorId);
	}

	@Transactional
	public void deleteVendorActivity(Integer id) {
		calendarEventRepository.deleteByVendorActivityId(id);
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

	public List<TopActivityDTO> getTop5Activities() {
		Pageable pageable = PageRequest.of(0, 5);
	        return activityRegistrationRepository.findTop5Activities(pageable);
	}

}
