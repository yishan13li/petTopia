package petTopia.controller.vendor_admin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.model.vendor.VendorActivityReview;
import petTopia.repository.vendor.VendorActivityReviewRepository;

@Controller
public class VendorActivityReviewsController {

	@Autowired
	private VendorActivityReviewRepository vendorActivityReviewRepository;

	@GetMapping("/vendor_admin/activityReviews")
	public String activityReviewsPage() {
		return "vendor_admin/NewFile1";
	}

	@ResponseBody
	@GetMapping("/api/vendor_admin/activityreviews")
	public ResponseEntity<?> getReviewsByVendorActivityId(@RequestParam Integer vendorActivityId) {
		List<VendorActivityReview> vendorActivityReviews = vendorActivityReviewRepository
				.findByVendorActivityId(vendorActivityId);
//		System.err.println(vendorActivityReviews);
		if (vendorActivityReviews.isEmpty()) {
			return ResponseEntity.ok(Collections.emptyList()); // ✅ 返回空数组 []
		}
		return new ResponseEntity<>(vendorActivityReviews, HttpStatus.OK);
	}

	@ResponseBody
	@DeleteMapping("api/vendor_admin/activityreviews/delete/{reviewId}")
	public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
		Optional<VendorActivityReview> review = vendorActivityReviewRepository.findById(reviewId);
		Map<String, String> response = new HashMap<>();
		if (review.isPresent()) {
			vendorActivityReviewRepository.deleteById(reviewId);

			response.put("message", "刪除成功");
		} else {
			response.put("message", "刪除失敗無此資料");
		}

		return ResponseEntity.ok(response);
	}

}
