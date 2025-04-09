package petTopia.service.vendor;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.ActivityTypeRepository;
import petTopia.util.ImageConverter;

@Service
public class VendorActivityService {

	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	@Autowired
	private ActivityTypeRepository activityTypeRepository;

	public List<VendorActivity> findAllActivity() {
		List<VendorActivity> activityList = vendorActivityRepository.findAll();

		for (VendorActivity activity : activityList) {
			byte[] logoImg = activity.getVendor().getLogoImg();
			if (logoImg != null) {
				String mimeType = ImageConverter.getMimeType(logoImg);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				activity.getVendor().setLogoImgBase64(base64);
			}

		}

		return activityList;

	}

	public VendorActivity findActivityById(Integer id) {
		VendorActivity activity = vendorActivityRepository.findById(id).orElse(null);

		byte[] logoImg = activity.getVendor().getLogoImg();
		if (logoImg != null) {
			String mimeType = ImageConverter.getMimeType(logoImg);
			String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
			activity.getVendor().setLogoImgBase64(base64);
		}

		return activity;
	}

	public List<VendorActivity> findAllActivityExceptOne(Integer activityId) {
		List<VendorActivity> activityList = vendorActivityRepository.findAll();
		VendorActivity activityToRemove = vendorActivityRepository.findById(activityId).orElse(null);

		if (activityToRemove != null) {
			activityList.removeIf(v -> v.getId().equals(activityToRemove.getId()));
		}

		return activityList;
	}

	public List<VendorActivity> findActivityByTypeId(Integer typeId) {
		ActivityType type = activityTypeRepository.findById(typeId).orElse(null);
		List<VendorActivity> activityList = vendorActivityRepository.findByActivityType(type);
		return activityList;
	}

	public List<VendorActivity> findActivityByTypeIdExceptOne(Integer typeId, Integer activityId) {
		ActivityType type = activityTypeRepository.findById(typeId).orElse(null);
		List<VendorActivity> activityList = vendorActivityRepository.findByActivityType(type);
		VendorActivity activityToRemove = vendorActivityRepository.findById(activityId).orElse(null);

		if (activityToRemove != null) {
			activityList.removeIf(activity -> activity.getId().equals(activityToRemove.getId())); // 刪除ID與activityToRemove相同ID相同之活動
		}

		return activityList;
	}

	/* 瀏覽數增加 */
	public VendorActivity increaseNumberOfVisitor(Integer activityId) {
		VendorActivity activity = vendorActivityRepository.findById(activityId).orElse(null);

		Integer numberVisitor = activity.getNumberVisitor();
		numberVisitor = numberVisitor + 1;

		activity.setNumberVisitor(numberVisitor);
		vendorActivityRepository.save(activity);

		return activity;
	}

	/* 模糊搜尋活動 */
	public List<VendorActivity> findVendorByNameOrDescription(String keyword) {
		List<VendorActivity> list1 = vendorActivityRepository.findByNameContaining(keyword);
		List<VendorActivity> list2 = vendorActivityRepository.findByDescriptionContaining(keyword);
		List<VendorActivity> list3 = vendorActivityRepository.findByAddressContaining(keyword);

		/* 使用set來過濾重複之資料 */
		Set<Integer> set = new HashSet<>();
		List<VendorActivity> finalList = new ArrayList<>();

		for (VendorActivity v : list1) {
			if (set.add(v.getId())) { // set.add(id)會回傳布林值，如果id沒出現過則加入
				finalList.add(v);
			}
		}

		for (VendorActivity v : list2) {
			if (set.add(v.getId())) {
				finalList.add(v);
			}
		}

		for (VendorActivity v : list3) {
			if (set.add(v.getId())) {
				finalList.add(v);
			}
		}

		return finalList;
	}

	/* 用店家ID搜尋活動 */
	public List<VendorActivity> findActivityListByVendorId(Integer vendorId) {
		List<VendorActivity> activityList = vendorActivityRepository.findByVendorId(vendorId);
		return activityList;
	}
}
