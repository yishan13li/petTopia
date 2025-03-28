package petTopia.controller.vendor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorReview;
import petTopia.service.vendor.ReviewPhotoService;
import petTopia.service.vendor.VendorReviewService;

@CrossOrigin
@RestController
public class VendorReviewController {

	@Autowired
	private VendorReviewService vendorReviewService;

	@Autowired
	private ReviewPhotoService reviewPhotoService;

	@GetMapping("/api/vendor/{vendorId}/review")
	public ResponseEntity<List<VendorReviewDto>> getVendorReview(@PathVariable Integer vendorId) {
		List<VendorReviewDto> reviewList = vendorReviewService.findReviewListByVendorId(vendorId);
		return ResponseEntity.ok(reviewList);
	}

	@GetMapping("/api/vendor/{vendorId}/update/rating")
	public ResponseEntity<Vendor> updateVendorRating(@PathVariable Integer vendorId) {
		Vendor vendor = vendorReviewService.setAverageRating(vendorId);
		return ResponseEntity.ok(vendor);
	}

	@GetMapping("/api/vendor/review/{reviewId}")
	public Map<String, Object> getVendorReviewById(@PathVariable Integer reviewId) {
		VendorReview review = vendorReviewService.findReviewById(reviewId);
		Map<String, Object> response = new HashMap<>();
		response.put("review", review);
		return response;
	}

	@GetMapping("/api/vendor/review/{reviewId}/photo")
	public ResponseEntity<List<ReviewPhoto>> getReviewPhoto(@PathVariable Integer reviewId) {
		List<ReviewPhoto> photoList = reviewPhotoService.findPhotoListByReviewId(reviewId);
		return ResponseEntity.ok(photoList);
	}

	@PostMapping(value = "/api/vendor/{vendorId}/review/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, Object> giveReview(@PathVariable Integer vendorId, @RequestParam Integer memberId,
			@RequestParam String content, @RequestPart(required = false) List<MultipartFile> reviewPhotos)
			throws IOException {
		VendorReview review = new VendorReview();
		if (reviewPhotos != null) {
			review = vendorReviewService.addReview(memberId, vendorId, content, reviewPhotos);
		} else {
			List<MultipartFile> nullList = new ArrayList<MultipartFile>();
			review = vendorReviewService.addReview(memberId, vendorId, content, nullList);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("review", review);
		return response;
	}

	@PostMapping(value = "/api/vendor/{vendorId}/review/star/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, Object> giveReviewStar(@PathVariable Integer vendorId, @RequestParam Integer memberId,
			@RequestParam Integer ratingEnv, @RequestParam Integer ratingPrice, @RequestParam Integer ratingService)
			throws IOException {
		VendorReview starReview = vendorReviewService.addStarReview(memberId, vendorId, ratingEnv, ratingPrice,
				ratingService);
		Map<String, Object> response = new HashMap<>();
		response.put("review", starReview);
		return response;
	}

	@PostMapping(value = "/api/vendor/review/{reviewId}/rewrite", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, Object> rewriteTextReview(@PathVariable Integer reviewId, @RequestParam String content) {
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

	@GetMapping("/api/vendor/{vendorId}/member/{memberId}/review/exist")
	public Map<String, Object> getReviewIsExisted(@PathVariable Integer vendorId, @PathVariable Integer memberId) {
		boolean isExisted = vendorReviewService.getReviewIsExisted(memberId, vendorId);
		Map<String, Object> response = new HashMap<>();
		response.put("action", isExisted ? true : false);
		return response;
	}

	@PostMapping(value = "/api/vendor/{vendorId}/review/add/final", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, Object> addReview(@PathVariable Integer vendorId, @RequestParam Integer memberId,
			@RequestParam String content, Integer ratingEnv, Integer ratingPrice, Integer ratingService,
			@RequestPart(required = false) List<MultipartFile> reviewPhotos) throws IOException {
		VendorReview review = new VendorReview();

		if (reviewPhotos == null) {
			reviewPhotos = new ArrayList<>();
		}

		review = vendorReviewService.addNewReview(memberId, vendorId, content, ratingEnv, ratingPrice, ratingService,
				reviewPhotos);

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("review", review);
		return response;
	}

	@PutMapping(value = "/api/vendor/review/{reviewId}/rewrite/final", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, Object> modifyReview(@PathVariable Integer reviewId, @RequestParam String content,
			Integer ratingEnv, Integer ratingPrice, Integer ratingService,
			@RequestPart(required = false) List<MultipartFile> reviewPhotos,
			@RequestParam(required = false) List<Integer> deletePhotoIds) throws IOException {
		if (reviewPhotos == null) {
			reviewPhotos = new ArrayList<>();
		}
		
	    if (deletePhotoIds == null) {
	        deletePhotoIds = new ArrayList<>();
	    }

		VendorReview review = vendorReviewService.modifyReview(reviewId, content, ratingEnv, ratingPrice, ratingService,
				reviewPhotos, deletePhotoIds);

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("review", review);
		return response;
	}
}
