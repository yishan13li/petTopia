package petTopia.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.VendorActivity;
import petTopia.service.vendor.ActivityTypeUserService;
import petTopia.service.vendor.VendorActivityService;

@CrossOrigin
@RestController
public class ActivityController {

	@Autowired
	private VendorActivityService vendorActivityService;
	
	@Autowired
	private ActivityTypeUserService activityTypeUserService;

	@GetMapping("api/activity/all")
	public ResponseEntity<List<VendorActivity>> getAllActivities() {
		List<VendorActivity> activityList = vendorActivityService.findAllActivity();
		return ResponseEntity.ok(activityList);
	}

	@GetMapping("api/activity/{activityId}")
	public ResponseEntity<VendorActivity> getActivityDetail(@PathVariable Integer activityId) {
		VendorActivity activity = vendorActivityService.findActivityById(activityId);
		return ResponseEntity.ok(activity);
	}

	@GetMapping("api/activity/all/except/{activityId}")
	public ResponseEntity<List<VendorActivity>> getAllActivitiesExceptOne(@PathVariable Integer activityId) {
		List<VendorActivity> activityList = vendorActivityService.findAllActivityExceptOne(activityId);
		return ResponseEntity.ok(activityList);
	}
	
	
	@GetMapping("/api/activity/type/{typeId}")
	public ResponseEntity<List<VendorActivity>> getVendorsByCategory(@PathVariable Integer typeId) {
		List<VendorActivity> activityList = vendorActivityService.findActivityByTypeId(typeId);
		return ResponseEntity.ok(activityList);
	}
	
	@GetMapping("/api/activity/type/show")
	public ResponseEntity<List<ActivityType>> getAllTypes() {
		List<ActivityType> typeList = activityTypeUserService.findAllActivityType();
		return ResponseEntity.ok(typeList);
	}

}
