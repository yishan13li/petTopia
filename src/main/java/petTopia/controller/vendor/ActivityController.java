package petTopia.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityReview;
import petTopia.service.vendor.VendorActivityReviewService;
import petTopia.service.vendor.VendorActivityService;

@CrossOrigin
@RestController
public class ActivityController {
	
	@Autowired
	private VendorActivityService vendorActivityService;
	
	@Autowired
	private VendorActivityReviewService vendorActivityReviewService;
		
	@GetMapping("/activity/all")
	public ResponseEntity<List<VendorActivity>> getAllActivities() {
		List<VendorActivity> activityList = vendorActivityService.findAllActivity();
		return ResponseEntity.ok(activityList);
	}
	
	@GetMapping("/activity/{activityId}")
	public ResponseEntity<VendorActivity> getActivityDetail(@PathVariable Integer activityId) {
		VendorActivity activity = vendorActivityService.findActivityById(activityId);
		return ResponseEntity.ok(activity);
	}
	
	@GetMapping("/activity/{activityId}/review")
	public ResponseEntity<List<VendorActivityReview>> getActivityReview(@PathVariable Integer activityId) {
		List<VendorActivityReview> reviewList = vendorActivityReviewService.findActivityReviewByVendorId(activityId);
		return ResponseEntity.ok(reviewList);
	}
}
