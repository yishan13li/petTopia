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

/**
 * 活動相關的控制器
 * 處理所有與活動相關的API請求
 */
@CrossOrigin
@RestController
public class ActivityController {
	
	@Autowired
	private VendorActivityService vendorActivityService;
	
	@Autowired
	private VendorActivityReviewService vendorActivityReviewService;
		
	/**
	 * 獲取所有活動列表
	 * @return 所有活動的列表
	 */
	@GetMapping("/activity/all")
	public ResponseEntity<List<VendorActivity>> getAllActivities() {
		List<VendorActivity> activityList = vendorActivityService.findAllActivity();
		return ResponseEntity.ok(activityList);
	}
	
	/**
	 * 獲取特定活動的詳細資訊
	 * @param activityId 活動ID
	 * @return 活動詳細資訊
	 */
	@GetMapping("/activity/{activityId}")
	public ResponseEntity<VendorActivity> getActivityDetail(@PathVariable Integer activityId) {
		VendorActivity activity = vendorActivityService.findActivityById(activityId);
		return ResponseEntity.ok(activity);
	}
	
	/**
	 * 獲取特定活動的評論列表
	 * @param activityId 活動ID
	 * @return 活動的評論列表
	 */
	@GetMapping("/activity/{activityId}/review")
	public ResponseEntity<List<VendorActivityReview>> getActivityReview(@PathVariable Integer activityId) {
		List<VendorActivityReview> reviewList = vendorActivityReviewService.findActivityReviewByVendorId(activityId);
		return ResponseEntity.ok(reviewList);
	}
}
