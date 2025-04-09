package petTopia.controller.vendor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityImages;
import petTopia.service.vendor.ActivityTypeUserService;
import petTopia.service.vendor.VendorActivityImagesService;
import petTopia.service.vendor.VendorActivityService;

@CrossOrigin
@RestController
public class ActivityController {

	@Autowired
	private VendorActivityService vendorActivityService;

	@Autowired
	private VendorActivityImagesService vendorActivityImagesService;

	@Autowired
	private ActivityTypeUserService activityTypeUserService;

	@GetMapping("/api/activity/all")
	public ResponseEntity<List<VendorActivity>> getAllActivities() {
		List<VendorActivity> activityList = vendorActivityService.findAllActivity();
		return ResponseEntity.ok(activityList);
	}

	@GetMapping("/api/activity/{activityId}")
	public ResponseEntity<VendorActivity> getActivityDetail(@PathVariable Integer activityId) {
		VendorActivity activity = vendorActivityService.findActivityById(activityId);
		return ResponseEntity.ok(activity);
	}

	@GetMapping("/api/activity/all/except/{activityId}")
	public ResponseEntity<List<VendorActivity>> getAllActivitiesExceptOne(@PathVariable Integer activityId) {
		List<VendorActivity> activityList = vendorActivityService.findAllActivityExceptOne(activityId);
		return ResponseEntity.ok(activityList);
	}

	@GetMapping("/api/activity/{activityId}/image")
	public ResponseEntity<List<VendorActivityImages>> getActivityImages(@PathVariable Integer activityId) {
		List<VendorActivityImages> imageList = vendorActivityImagesService.findImageListByActivityId(activityId);
		return ResponseEntity.ok(imageList);
	}

	@GetMapping("/api/activity/type/{typeId}")
	public ResponseEntity<List<VendorActivity>> getActivitiesByType(@PathVariable Integer typeId) {
		List<VendorActivity> activityList = vendorActivityService.findActivityByTypeId(typeId);
		return ResponseEntity.ok(activityList);
	}

	@GetMapping("/api/activity/type/{typeId}/except/activity/{activityId}")
	public ResponseEntity<List<VendorActivity>> getActivitiesByCategoryExceptOne(@PathVariable Integer typeId,
			@PathVariable Integer activityId) {
		List<VendorActivity> activityList = vendorActivityService.findActivityByTypeIdExceptOne(typeId, activityId);
		return ResponseEntity.ok(activityList);
	}

	@PostMapping("/api/activity/find")
	public ResponseEntity<List<VendorActivity>> getActivitiesByKeyword(@RequestBody Map<String, String> data) {
		String keyword = data.get("keyword");
		List<VendorActivity> activityList = vendorActivityService.findVendorByNameOrDescription(keyword);
		return ResponseEntity.ok(activityList);
	}

	@GetMapping("/api/activity/type/show")
	public ResponseEntity<List<ActivityType>> getAllTypes() {
		List<ActivityType> typeList = activityTypeUserService.findAllActivityType();
		return ResponseEntity.ok(typeList);
	}

	@GetMapping("/api/activity/{activityId}/increase/number/visitor")
	public ResponseEntity<VendorActivity> increaseNumberOfVisitor(@PathVariable Integer activityId) {
		VendorActivity activity = vendorActivityService.increaseNumberOfVisitor(activityId);
		return ResponseEntity.ok(activity);
	}

	@GetMapping("/api/activity/vendor/{vendorId}")
	public ResponseEntity<List<VendorActivity>> getActivitiesByVendorId(@PathVariable Integer vendorId) {
		List<VendorActivity> activityList = vendorActivityService.findActivityListByVendorId(vendorId);
		return ResponseEntity.ok(activityList);
	}
}
