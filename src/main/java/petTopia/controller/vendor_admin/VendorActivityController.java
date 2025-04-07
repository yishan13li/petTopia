package petTopia.controller.vendor_admin;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.vendor_admin.TopActivityDTO;
import petTopia.model.vendor.ActivityPeopleNumber;
import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.CalendarEvent;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityImages;
import petTopia.repository.vendor.CalendarEventRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor_admin.ActivityPeopleNumberRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;
import petTopia.service.vendor_admin.ActivityTypeService;
import petTopia.service.vendor_admin.VendorActivityServiceAdmin;

@Controller
public class VendorActivityController {

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private VendorActivityServiceAdmin vendorActivityServiceAdmin;

	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	@Autowired
	private VendorActivityImagesRepository vendorActivityImagesRepository;

	@Autowired
	private ActivityTypeService activityTypeService;

	@Autowired
	private ActivityPeopleNumberRepository activityPeopleNumberRepository;

	@Autowired
	private CalendarEventRepository calendarEventRepository;

	@Autowired
	private ActivityRegistrationRepository activityRegistrationRepository;

	@ResponseBody
	@GetMapping("/api/vendor_admin/activity/top5")
	public ResponseEntity<List<TopActivityDTO>> getTop5Activities() {
		List<TopActivityDTO> topActivities = vendorActivityServiceAdmin.getTop5Activities();
		return ResponseEntity.ok(topActivities);
	}

	public void updateActivityCount(Vendor vendor) {
		int activityCount = vendorActivityRepository.countByVendor(vendor);
		vendor.setEventCount(activityCount);

		// **根据活动数量更新 level**
		if (activityCount > 8) {
			vendor.setVendorLevel("頂級"); // 例如：50场以上是白金等级
		} else if (activityCount > 5) {
			vendor.setVendorLevel("資深"); // 20-49场是黄金等级
		} else if (activityCount > 2) {
			vendor.setVendorLevel("進階"); // 10-19场是白银等级
		} else {
			vendor.setVendorLevel("普通"); // 10场以下是青铜等级
		}
		vendorRepository.save(vendor); // 更新活动数量到数据库
	}
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
		Optional<VendorActivity> activity = vendorActivityServiceAdmin.getVendorActivityById(id);
		List<Integer> vendorActivityImageIdList = new ArrayList<>();
		if (activity.isPresent()) {
			VendorActivity vendorActivity = activity.get();
			System.out.println(vendorActivity);
			ActivityPeopleNumber activityPeopleNumber = vendorActivity.getActivityPeopleNumber();
			List<ActivityType> activityTypes = activityTypeService.getAllActivityTypes();
			List<Map<String, Object>> registrationOptions = new ArrayList<>();
			Map<String, Object> option1 = new HashMap<>();
			option1.put("value", "true");
			option1.put("label", "需要報名");
			registrationOptions.add(option1);

			Map<String, Object> option2 = new HashMap<>();
			option2.put("value", "false");
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

	@ResponseBody
	@GetMapping("api/vendor_admin/vendor_admin_activityDetail")
	public ResponseEntity<?> getVendorActivityDetail(@RequestParam Integer id) {
		Optional<VendorActivity> activity = vendorActivityServiceAdmin.getVendorActivityById(id);
		if (activity.isPresent()) {
			VendorActivity vendorActivity = activity.get();
			ActivityPeopleNumber activityPeopleNumber = vendorActivity.getActivityPeopleNumber();
			List<ActivityType> activityTypes = activityTypeService.getAllActivityTypes();

			// 轉換成 JSON
			Map<String, Object> response = new HashMap<>();
			response.put("vendorActivity", vendorActivity);
			response.put("vendorActivityImageIdList",
					vendorActivity.getImages().stream().map(VendorActivityImages::getId).toList());
			response.put("activityPeopleNumber", activityPeopleNumber);
			response.put("activityTypes", activityTypes);

			// 報名選項
			List<Map<String, Object>> registrationOptions = new ArrayList<>();
			registrationOptions.add(Map.of("value", "true", "label", "需要報名"));
			registrationOptions.add(Map.of("value", "false", "label", "不需報名"));
			response.put("registrationOptions", registrationOptions);

			return ResponseEntity.ok(response);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("活動不存在");
	}

	@GetMapping("/vendor_admin/activity/addPage")
	public String showAddActivityPage(Model model) {
		List<ActivityType> activityTypes = activityTypeService.getAllActivityTypes();
		model.addAttribute("activityTypes", activityTypes);

		// 轉換 is_registration_required 選單的值 (0 -> "不需報名", 1 -> "需要報名")
		List<Map<String, Object>> registrationOptions = new ArrayList<>();
		Map<String, Object> option1 = new HashMap<>();
		option1.put("value", "true");
		option1.put("label", "需要報名");
		registrationOptions.add(option1);

		Map<String, Object> option2 = new HashMap<>();
		option2.put("value", "false");
		option2.put("label", "不需報名");
		registrationOptions.add(option2);

		model.addAttribute("registrationOptions", registrationOptions);
		return "/vendor_admin/vendor_admin_addactivity"; // Thymeleaf 頁面名稱
	}

	@ResponseBody
	@GetMapping("/api/vendor_admin/activity/allTypes")
	public ResponseEntity<List<ActivityType>> getAllActivityTypes() {
		List<ActivityType> types = activityTypeService.getAllActivityTypes();
		return ResponseEntity.ok(types);
	}

	@GetMapping("/api/vendor_admin/activity/checkConflictDetail")
	public ResponseEntity<Boolean> checkActivityTimeConflictDetail(@RequestParam("vendorId") Integer vendorId,@RequestParam("activityId") Integer activityId,@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
	    boolean conflictExists = vendorActivityServiceAdmin.checkTimeConflictDetail(vendorId,activityId,startTime, endTime);
	    return ResponseEntity.ok(conflictExists);
	}
	
	@GetMapping("/api/vendor_admin/activity/checkConflict")
	public ResponseEntity<Boolean> checkActivityTimeConflict(@RequestParam("vendorId") Integer vendorId,@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
	    boolean conflictExists = vendorActivityServiceAdmin.checkTimeConflict(vendorId,startTime, endTime);
	    return ResponseEntity.ok(conflictExists);
	}
	
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

			Boolean isRegistrationRequired = Boolean.parseBoolean(is_registration_required);
			vendorActivity.setIsRegistrationRequired(isRegistrationRequired); // 設置布林值
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

			updateActivityCount(vendor);

			CalendarEvent calendarEvent = new CalendarEvent();
			calendarEvent.setEventTitle(activity_name);
			calendarEvent.setStartTime(startTime);
			calendarEvent.setEndTime(endTime);
			calendarEvent.setVendorActivity(vendorActivity);
			calendarEvent.setVendor(vendor);
			calendarEvent.setCreatedAt(new Date());
			calendarEvent.setUpdatedAt(new Date());
			calendarEvent.setColor("#ffb8b8");
			calendarEventRepository.save(calendarEvent);

			return new ResponseEntity<>(HttpStatus.CREATED); // 201
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400
		}

	}

	@ResponseBody
	@PostMapping("/api/vendor_activity/update")
	public ResponseEntity<?> updateActivity(@RequestParam("activity_id") Integer activityId,
			@RequestParam("vendor_id") Integer vendorId, @RequestParam("activity_name") String activity_name,
			@RequestParam ActivityType activity_type_id, @RequestParam String activity_description,
			@RequestParam String activity_address,
			@RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
			@RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
			@RequestParam String is_registration_required, @RequestParam Integer max_participants,
			@RequestParam(value = "files", required = false) MultipartFile[] files,
			@RequestParam(value = "deletedImageIds", required = false) List<Integer> deletedImageIds) {

		try {
			// 1. 查找活動
			VendorActivity vendorActivity = vendorActivityRepository.findById(activityId)
					.orElseThrow(() -> new Exception("活動不存在"));

			// 2. 更新基本資訊
			vendorActivity.setName(activity_name);
			vendorActivity.setDescription(activity_description);
			vendorActivity.setAddress(activity_address);
			vendorActivity.setStartTime(startTime);
			vendorActivity.setEndTime(endTime);
			vendorActivity.setActivityType(activity_type_id);

			Boolean isRegistrationRequired = Boolean.parseBoolean(is_registration_required);
			vendorActivity.setIsRegistrationRequired(isRegistrationRequired); // 設置布林值

			// 3. 刪除指定的圖片
			if (deletedImageIds != null && !deletedImageIds.isEmpty()) {
				vendorActivityImagesRepository.deleteAllById(deletedImageIds);
			}

			// 4. 更新新圖片（如果有新圖片則更新）
			if (files != null && files.length > 0) {
				List<VendorActivityImages> vendorActivityImagesList = new ArrayList<>();
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						System.out.println("上傳的圖片：" + file.getOriginalFilename());
						VendorActivityImages vendorActivityImage = new VendorActivityImages();
						vendorActivityImage.setImage(file.getBytes());
						vendorActivityImage.setVendorActivity(vendorActivity);
						vendorActivityImagesList.add(vendorActivityImage);
						System.out.println(vendorActivityImagesList);
					}
				}
				vendorActivity.getImages().addAll(vendorActivityImagesList);
			}

			// 5. 更新最大參與人數
			ActivityPeopleNumber activityPeopleNumber = activityPeopleNumberRepository
					.findByVendorActivity_Id(activityId).orElseThrow(() -> new Exception("人數表不存在"));
			activityPeopleNumber.setMaxParticipants(max_participants);

			activityPeopleNumberRepository.save(activityPeopleNumber);

			if (!isRegistrationRequired) {
				activityRegistrationRepository.deleteByVendorActivityId(activityId);

				activityPeopleNumber.setCurrentParticipants(0);

				activityPeopleNumberRepository.save(activityPeopleNumber);
			}

			// 6. 儲存變更
			vendorActivityRepository.save(vendorActivity);

			Optional<CalendarEvent> calendarOpt = calendarEventRepository.findByVendorActivityId(activityId);
			if (calendarOpt.isPresent()) {
				System.out.println(calendarOpt);
				CalendarEvent calendarEvent = calendarOpt.get(); // 取出对象
				calendarEvent.setEventTitle(activity_name);
				calendarEvent.setStartTime(startTime);
				calendarEvent.setEndTime(endTime);
				calendarEvent.setUpdatedAt(new Date());
				calendarEventRepository.save(calendarEvent);

				return new ResponseEntity<>(calendarEvent, HttpStatus.OK); // 返回 200 OK
			}

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@ResponseBody
	@GetMapping("/api/vendor_admin/activity/{vendorId}")
	public ResponseEntity<List<VendorActivity>> getVendorActivitiesByVendorId(@PathVariable Integer vendorId) {
		List<VendorActivity> activities = vendorActivityServiceAdmin.getVendorActivityByVendorId(vendorId);

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
		Optional<VendorActivity> vendorActivity = vendorActivityRepository.findById(id);
		vendorActivityServiceAdmin.deleteVendorActivity(id);
		Vendor vendor = vendorActivity.get().getVendor();
		updateActivityCount(vendor);
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

	// 計算並更新店家的等級
//	private void updateVendorLevel(Vendor vendor) {
//	    // 計算總活動數和評分來決定等級
//	    String newLevel = calculateVendorLevel(vendor);
//	    vendor.setVendorLevel(newLevel);  // 更新等級
//	    vendorRepository.save(vendor);  // 儲存更新後的 Vendor
//	}

	// 計算等級的邏輯
//	private String calculateVendorLevel(Vendor vendor) {
//	    int totalPosts = vendor.getTotalActivityCount();  // 店家總發文數
//	    double averageRating = vendor.getAverageRating(); // 假設 Vendor 有計算過的平均評分
//
//	    if (totalPosts >= 50 && averageRating >= 4.5) {
//	        return "Gold";  // 等級為 Gold
//	    } else if (totalPosts >= 30 && averageRating >= 4.0) {
//	        return "Silver";  // 等級為 Silver
//	    } else if (totalPosts >= 10 && averageRating >= 3.5) {
//	        return "Bronze";  // 等級為 Bronze
//	    } else {
//	        return "Standard";  // 等級為 Standard
//	    }
//	}
}
