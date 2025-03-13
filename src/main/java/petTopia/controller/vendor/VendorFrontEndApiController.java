package petTopia.controller.vendor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorReview;
import petTopia.service.vendor.VendorLikeService;
import petTopia.service.vendor.VendorReviewService;
import petTopia.service.vendor.VendorService;

@CrossOrigin
@RestController
public class VendorFrontEndApiController {

	@Autowired
	private VendorService vendorService;

	@Autowired
	private VendorReviewService vendorReviewService;

	@Autowired
	private VendorLikeService vendorLikeService;

	@PostMapping("/vendor/give_text_review")
	public Map<String, Object> giveTextReview(@RequestBody Map<String, Object> data) {
		Integer memberId = (Integer) data.get("memberId");
		Integer vendorId = (Integer) data.get("vendorId");
		String content = (String) data.get("content");

		vendorReviewService.addOrModifyVendorTextReview(memberId, vendorId, content);

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		return response;
	}

	@PostMapping("/vendor/give_star_review")
	public Map<String, Object> giveStarReview(@RequestBody Map<String, Integer> data) {
		Integer memberId = data.get("memberId");
		Integer vendorId = data.get("vendorId");
		Integer ratingEnv = data.get("ratingEnv");
		Integer ratingPrice = data.get("ratingPrice");
		Integer ratingService = data.get("ratingService");
		vendorReviewService.addOrModifyVendorStarReview(memberId, vendorId, ratingEnv, ratingPrice, ratingService);

		Map<String, Object> response = new HashMap<>();
		response.put("sucess", true);
		return response;
	}

	@PostMapping("/vendor/give_vendor_like")
	public Map<String, Object> giveVendorLike(@RequestBody Map<String, Integer> data) {
		Integer memberId = data.get("memberId");
		Integer vendorId = data.get("vendorId");

		boolean isLiked = vendorLikeService.addOrCancelVendorLike(memberId, vendorId);

		Map<String, Object> response = new HashMap<>();
		response.put("action", isLiked ? true : false);
		return response;
	}

	@GetMapping("/vendor/find_by_category")
	public List<Vendor> findVendorByCategory(Integer categoryId) {
		List<Vendor> vendorList = vendorService.findVendorByCategoryId(categoryId);
		return vendorList;
	}

	@GetMapping("/vendor/find_by_keyword")
	public List<Vendor> findVendorByKeyword(String keyword) {
		List<Vendor> vendorList = vendorService.findVendorByNameOrDescription(keyword);
		return vendorList;
	}

	@PostMapping("/vendor/add_review_photos")
	public ResponseEntity<Map<String, Object>> addReviewPhotos(Integer reviewId,
			@RequestPart List<MultipartFile> reviewPhotos) throws IOException {

		vendorReviewService.addReviewPhotos(reviewId, reviewPhotos);

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Photos uploaded successfully");
		response.put("reviewId", reviewId);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/* API FOR VUE BELOW */
	/* API FOR VUE BELOW */
	/* API FOR VUE BELOW */

	@PostMapping("/api/vendor/{vendorId}/like/toggle")
	public Map<String, Object> toggleLike(@PathVariable Integer vendorId, @RequestBody Map<String, Integer> data) {

		Integer memberId = data.get("memberId");
		boolean isLiked = vendorLikeService.addOrCancelVendorLike(memberId, vendorId);

		Map<String, Object> response = new HashMap<>();
		response.put("action", isLiked ? true : false);
		return response;
	}

	@PostMapping(value = "/api/vendor/{vendorId}/review/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 確保 Spring 能解析 multipart/form-data
	public Map<String, Object> giveReview(@PathVariable Integer vendorId, @RequestParam Integer memberId,
			@RequestParam String content, @RequestPart(required = false) List<MultipartFile> reviewPhotos)
			throws IOException {
		if(reviewPhotos!=null) {			
			vendorReviewService.addReview(memberId, vendorId, content, reviewPhotos);
		}else {
			List<MultipartFile> nullList = new ArrayList<MultipartFile>();
			vendorReviewService.addReview(memberId, vendorId, content, nullList);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		return response;
	}

	@GetMapping("/api/vendor/review/{reviewId}")
	public Map<String, Object> getVendorReviewById(@PathVariable Integer reviewId) {
		VendorReview review = vendorReviewService.findReviewById(reviewId);

		Map<String, Object> response = new HashMap<>();
		response.put("review", review);

		return response;
	}

	@PostMapping("/api/vendor/review/{reviewId}/rewrite")
	public Map<String, Object> rewriteTextReview(@PathVariable Integer reviewId,
			@RequestBody Map<String, Object> data) {
		String content = (String) data.get("content");

		VendorReview review = vendorReviewService.rewriteReviewById(reviewId, content);

		Map<String, Object> response = new HashMap<>();
		response.put("review", review);
		return response;
	}

	@DeleteMapping("/api/vendor/review/{reviewId}/delete")
	public Map<String, Object> deleteReview(@PathVariable Integer reviewId) {

		vendorReviewService.deleteReviewById(reviewId);
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		return response;
	}

}
