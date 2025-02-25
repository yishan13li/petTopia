package petTopia.controller.vendor_admin;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.vendor_admin.ActivityPeopleNumber;
import petTopia.model.vendor_admin.ActivityType;
import petTopia.model.vendor_admin.Vendor;
import petTopia.model.vendor_admin.VendorActivity;
import petTopia.model.vendor_admin.VendorActivityImages;
import petTopia.repository.vendor_admin.ActivityPeopleNumberRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;
import petTopia.repository.vendor_admin.VendorActivityRepository;
import petTopia.repository.vendor_admin.VendorRepository;
import petTopia.service.vendor_admin.ActivityTypeService;
import petTopia.service.vendor_admin.VendorActivityService;

@Controller
public class VendorActivitivityController {

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private VendorActivityService vendorActivityService;

	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	@Autowired
	private VendorActivityImagesRepository vendorActivityImagesRepository;

	@Autowired
	private ActivityTypeService activityTypeService;

	@Autowired
	private ActivityPeopleNumberRepository activityPeopleNumberRepository;
//	@GetMapping
//	public List<VendorActivity> getAllVendorActivities() {
//		return vendorActivityService.getAllVendorActivities();
//	}

//	@GetMapping("/vendor_admin/activity/addPage")
//	public String getVendorActivityAddPage() {
//		return "/vendor_admin/vendor_admin_addactivity";
//	}

	@GetMapping("/vendor_admin/vendor_admin_activity")
	public String getVendorActivityPage() {
		return "vendor_admin/vendor_admin_activity"; // Thymeleaf 模板名稱
	}

	@GetMapping("/vendor_admin/vendor_admin_activityDetail")
	public String getVendorActivityDetail(@RequestParam Integer id, Model model) {
		Optional<VendorActivity> activity = vendorActivityService.getVendorActivityById(id);
		List<Integer> vendorActivityImageIdList = new ArrayList<>();
		if (activity.isPresent()) {
			VendorActivity vendorActivity = activity.get();
			System.out.println(vendorActivity);
			ActivityPeopleNumber activityPeopleNumber = vendorActivity.getActivityPeopleNumber();
			List<ActivityType> activityTypes = activityTypeService.getAllActivityTypes();
			List<Map<String, Object>> registrationOptions = new ArrayList<>();
			Map<String, Object> option1 = new HashMap<>();
			option1.put("value", 1);
			option1.put("label", "需要報名");
			registrationOptions.add(option1);

			Map<String, Object> option2 = new HashMap<>();
			option2.put("value", 0);
			option2.put("label", "不需報名");
			registrationOptions.add(option2);

			List<VendorActivityImages> vendorActivityImageList = vendorActivity.getImages();
			for (VendorActivityImages oneImage : vendorActivityImageList) {
				Integer imageId = oneImage.getId();
				vendorActivityImageIdList.add(imageId);	
			}
			model.addAttribute("vendorActivity", vendorActivity);
			model.addAttribute("vendorActivityImageIdList", vendorActivityImageIdList);
			model.addAttribute("activityPeopleNumber", activityPeopleNumber);
			model.addAttribute("activityTypes", activityTypes);
			model.addAttribute("registrationOptions", registrationOptions);
			return "/vendor_admin/vendor_admin_activitydetail";
		}
		return "error";
	}

	@GetMapping("/vendor_admin/activity/addPage")
	public String showAddActivityPage(Model model) {
		List<ActivityType> activityTypes = activityTypeService.getAllActivityTypes();
		model.addAttribute("activityTypes", activityTypes);

		// 轉換 is_registration_required 選單的值 (0 -> "不需報名", 1 -> "需要報名")
		List<Map<String, Object>> registrationOptions = new ArrayList<>();
		Map<String, Object> option1 = new HashMap<>();
		option1.put("value", 1);
		option1.put("label", "需要報名");
		registrationOptions.add(option1);

		Map<String, Object> option2 = new HashMap<>();
		option2.put("value", 0);
		option2.put("label", "不需報名");
		registrationOptions.add(option2);

		model.addAttribute("registrationOptions", registrationOptions);
		return "/vendor_admin/vendor_admin_addactivity"; // Thymeleaf 頁面名稱
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

//	@PostMapping("/vendor_admin/vendor_admin_activity/add")
//	public VendorActivity createVendorActivity(@RequestBody VendorActivity vendorActivity) {
//		return vendorActivityService.saveVendorActivity(vendorActivity);
//	}

	@ResponseBody
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteVendorActivity(@PathVariable Integer id) {
		vendorActivityService.deleteVendorActivity(id);
		Map<String, String> response = new HashMap<>();
		response.put("message", "刪除成功");
		return ResponseEntity.ok(response);
	}

	@GetMapping("/photos/download")
	public ResponseEntity<?> downloadPhotoById(@RequestParam Integer photoId) {
		Optional<VendorActivityImages> imageOpt = vendorActivityImagesRepository.findById(photoId);

		if (imageOpt.isPresent()) {
			VendorActivityImages image = imageOpt.get();
			byte[] imageFile = image.getImage(); // 假設每個 VendorActivityPhoto 實體有一個 photoFile 字段，存儲圖片二進制數據

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG); // 假設圖片是 JPEG 格式

			return new ResponseEntity<>(imageFile, headers, HttpStatus.OK); // 返回圖片的二進制數據
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 如果找不到圖片，返回 404
	}

//	@GetMapping("/photos/first-id")
//	public ResponseEntity<?> findFirstPhotoIdByVendorActivityId(@RequestParam Integer vendorActivityId) {
//		// 调用 repository 方法，获取活动的第一张图片 ID
//		Optional<Integer> firstImageId = vendorActivityService.getFirstImageIdByVendorActivityId(vendorActivityId);
//
//		if (firstImageId.isPresent()) {
//			// 如果找到了图片 ID，返回该 ID
//			return new ResponseEntity<>(firstImageId.get(), HttpStatus.OK);
//		}
//
//		// 如果没有找到活动或者图片，返回 404 Not Found
//		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//	}

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

	@GetMapping("/photos/idss")
	public ResponseEntity<?> findPhotoIdsByVendorActivityIds(@RequestParam List<Integer> vendorActivityIds) {
		List<Map<String, Object>> result = new ArrayList<>();

		for (Integer vendorActivityId : vendorActivityIds) {
			Optional<VendorActivity> op = vendorActivityRepository.findById(vendorActivityId);

			if (op.isPresent()) {
				VendorActivity vendorActivity = op.get();
				List<VendorActivityImages> images = vendorActivity.getVendorActivityImages();

				List<Integer> imageIdList = new ArrayList<>();
				for (VendorActivityImages image : images) {
					imageIdList.add(image.getId()); // 收集圖片 ID
				}

				Map<String, Object> data = new HashMap<>();
				data.put("vendorActivityId", vendorActivityId);
				data.put("imageIds", imageIdList);

				result.add(data); // 添加活動的圖片 ID 信息
			}
		}

		return new ResponseEntity<>(result, HttpStatus.OK); // 返回所有活動的圖片 ID 列表
	}

//	@ResponseBody
//	@PostMapping("/add")
//	public ResponseEntity<String> addActivity(@RequestBody VendorActivity activity) {
//		vendorActivityService.addActivity(activity);
//		return ResponseEntity.ok("活動新增成功");
//	}

	@ResponseBody
	@PostMapping("/api/vendor_activity/add") // 不只可以送json 也可以送@RequestParam
	public ResponseEntity<?> addActivity(@RequestParam("vendor_id") Integer vendorId,
			@RequestParam String activity_name, @RequestParam ActivityType activity_type_id,
			@RequestParam String activity_description, @RequestParam String activity_address,
			@RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
			@RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
			@RequestParam String is_registration_required, @RequestParam Integer max_participants,
			@RequestParam("files") MultipartFile[] files) {

		try {
			VendorActivity vendorActivity = new VendorActivity();
			Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new Exception("Vendor not found"));
			vendorActivity.setVendor(vendor);
			vendorActivity.setName(activity_name);
			vendorActivity.setActivityType(activity_type_id);
			vendorActivity.setDescription(activity_description);
			vendorActivity.setAddress(activity_address);
			vendorActivity.setStartTime(startTime);
			vendorActivity.setEndTime(endTime);

			List<VendorActivityImages> vendorActivityImagesList = new ArrayList<>();

			for (MultipartFile oneFile : files) {
				VendorActivityImages vendorActivityImages = new VendorActivityImages();
				vendorActivityImages.setImage(oneFile.getBytes());
				vendorActivityImages.setVendorActivity(vendorActivity); // 多set 一

				vendorActivityImagesList.add(vendorActivityImages);
			}

			vendorActivity.setImages(vendorActivityImagesList); // 一set多

			vendorActivity = vendorActivityRepository.save(vendorActivity);

			// 5. 建立並儲存人數表 (ActivityPeopleNumber)
			ActivityPeopleNumber activityPeopleNumber = new ActivityPeopleNumber();
			activityPeopleNumber.setVendorActivity(vendorActivity);
			activityPeopleNumber.setMaxParticipants(max_participants);
			activityPeopleNumber.setCurrentParticipants(0); // 初始參與人數設為 0
			activityPeopleNumberRepository.save(activityPeopleNumber);
			return new ResponseEntity<>(HttpStatus.CREATED); // 201
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400
		}

	}
}
