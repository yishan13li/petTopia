package petTopia.controller.vendor_admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.model.vendor_admin.VendorActivity;
import petTopia.model.vendor_admin.VendorActivityImages;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;
import petTopia.repository.vendor_admin.VendorActivityRepository;
import petTopia.service.vendor_admin.VendorActivityService;

@Controller
public class VendorActivitivityController {
	@Autowired
	private VendorActivityService vendorActivityService;
	
	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	@Autowired
	private VendorActivityImagesRepository vendorActivityImagesRepository;

//	@GetMapping
//	public List<VendorActivity> getAllVendorActivities() {
//		return vendorActivityService.getAllVendorActivities();
//	}
	@GetMapping("/vendor_admin/vendor_admin_activity")
	public String getVendorActivityPage() {
		return "vendor_admin/vendor_admin_activity"; // Thymeleaf 模板名稱
	}

	@ResponseBody
	@GetMapping("/api/vendor/activity/{vendorId}")
	public ResponseEntity<List<VendorActivity>> getVendorActivitiesByVendorId(@PathVariable Integer vendorId) {
		List<VendorActivity> activities = vendorActivityService.getVendorActivityByVendorId(vendorId);

		if (!activities.isEmpty()) {
			return ResponseEntity.ok(activities);
		}

		return ResponseEntity.status(404).body(null);
	}

	@PostMapping
	public VendorActivity createVendorActivity(@RequestBody VendorActivity vendorActivity) {
		return vendorActivityService.saveVendorActivity(vendorActivity);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteVendorActivity(@PathVariable Integer id) {
		vendorActivityService.deleteVendorActivity(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/photos/download")
	public ResponseEntity<?> downloadPhotoById(@RequestParam Integer photoId) {
	    Optional<VendorActivityImages> imageOpt = vendorActivityImagesRepository.findById(photoId);

	    if (imageOpt.isPresent()) {
	        VendorActivityImages image = imageOpt.get();
	        byte[] imageFile = image.getImage(); // 假設每個 VendorActivityPhoto 實體有一個 photoFile 字段，存儲圖片二進制數據

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.IMAGE_JPEG);  // 假設圖片是 JPEG 格式

	        return new ResponseEntity<>(imageFile, headers, HttpStatus.OK);  // 返回圖片的二進制數據
	    }

	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 如果找不到圖片，返回 404
	}

	@GetMapping("/photos/ids")
	public ResponseEntity<?> findPhotoIdsByVendorActivityId(@RequestParam Integer vendorActivityId) {
		Optional<VendorActivity> op = vendorActivityRepository.findById(vendorActivityId);

		List<Integer> imageIdList = new ArrayList<>();

		if (op.isPresent()) {
			VendorActivity vendorActivity = op.get();
			List<VendorActivityImages> images = vendorActivity.getVendorActivityImages();

			for (VendorActivityImages image : images) {
				imageIdList.add(image.getId()); // 假設每個 VendorActivityPhoto 實體有一個 id 字段
			}

			return new ResponseEntity<>(imageIdList, HttpStatus.OK); // 返回所有照片的 ID 列表
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 如果沒有找到活動，返回 404
	}
}
