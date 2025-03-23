package petTopia.controller.vendor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.vendor.ActivityReviewDto;
import petTopia.model.vendor.VendorActivityReview;
import petTopia.service.vendor.VendorActivityReviewService;

@RestController
public class ActivityReviewController {

	@Autowired
	private VendorActivityReviewService vendorActivityReviewService;

	@GetMapping("/api/activity/{activityId}/review")
	public ResponseEntity<List<ActivityReviewDto>> getActivityReview(@PathVariable Integer activityId) {
		List<ActivityReviewDto> reviewList = vendorActivityReviewService.findReviewListByActivityId(activityId);
		return ResponseEntity.ok(reviewList);
	}

	@GetMapping("/api/activity/review/{reviewId}")
	public Map<String, Object> getActivityReviewById(@PathVariable Integer reviewId) {
		VendorActivityReview review = vendorActivityReviewService.findReviewById(reviewId);
		Map<String, Object> response = new HashMap<>();
		response.put("review", review);
		return response;
	}
	
	@PostMapping("/api/activity/{activityId}/review/add")
	public Map<String, Object> addReview(@PathVariable Integer activityId, @RequestBody Map<String, String> data) {
		Integer memberId = Integer.parseInt(data.get("memberId"));
		String content = data.get("content");
		VendorActivityReview review = vendorActivityReviewService.addReview(memberId, activityId, content);
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("review", review);
		return response;
	}

	@PutMapping("/api/activity/review/{reviewId}/rewrite")
	public Map<String, Object> rewriteReview(@PathVariable Integer reviewId, @RequestBody Map<String, String> data) {
		String content = data.get("content");
		VendorActivityReview review = vendorActivityReviewService.rewriteReviewById(reviewId, content);
		Map<String, Object> response = new HashMap<>();
		response.put("review", review);
		return response;
	}

	@DeleteMapping("/api/activity/review/{reviewId}/delete")
	public Map<String, Object> deleteReview(@PathVariable Integer reviewId) {
		vendorActivityReviewService.deleteReviewById(reviewId);
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		return response;
	}
	
	@GetMapping("/api/activity/{activityId}/member/{memberId}/review/exist")
	public Map<String, Object> getReviewIsExisted(@PathVariable Integer activityId,@PathVariable Integer memberId) {
		boolean isExisted = vendorActivityReviewService.getReviewIsExisted(memberId,activityId);
		Map<String, Object> response = new HashMap<>();
		response.put("action", isExisted ? true : false);
		return response;
	}
}
