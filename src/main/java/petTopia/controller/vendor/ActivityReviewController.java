package petTopia.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import petTopia.model.vendor.VendorActivityReview;
import petTopia.service.vendor.VendorActivityReviewService;

@Controller
public class ActivityReviewController {

	@Autowired
	private VendorActivityReviewService vendorActivityReviewService;

	@GetMapping("api/activity/{activityId}/review")
	public ResponseEntity<List<VendorActivityReview>> getActivityReview(@PathVariable Integer activityId) {
		List<VendorActivityReview> reviewList = vendorActivityReviewService.findActivityReviewByVendorId(activityId);
		return ResponseEntity.ok(reviewList);
	}
}
