package petTopia.controller.vendor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.vendor.Vendor;
import petTopia.service.vendor.VendorLikeService;
import petTopia.service.vendor.VendorReviewService;
import petTopia.service.vendor.VendorService;

@Controller
public class VendorFrontEndApiController {

	@Autowired
	private VendorService vendorService;

	@Autowired
	private VendorReviewService vendorReviewService;

	@Autowired
	private VendorLikeService vendorLikeService;

//	@PostMapping("/vendor/give_text_review")
//	public String giveTextReview(Integer memberId, Integer vendorId, String context) {
//		vendorReviewService.addOrModifyVendorTextReview(memberId, vendorId, context);
//
//		return "/vendor/vendor_detail.html";
//	}
	
	@ResponseBody
	@PostMapping("/vendor/give_text_review")
	public Map<String, Object> giveTextReview(@RequestBody Map<String, Object> data) {
		Integer memberId = (Integer)data.get("memberId");
		Integer vendorId = (Integer)data.get("vendorId");
		String content = (String)data.get("content");

		vendorReviewService.addOrModifyVendorTextReview(memberId, vendorId, content);
		
		Map<String, Object> response = new HashMap<>();
		response.put("sucess", true);
		return response;
	}

//	@PostMapping("/vendor/give_star_review")
//	public String giveStarReview(Integer memberId, Integer vendorId, Integer ratingEnv, Integer ratingPrice,
//			Integer ratingService) {
//		vendorReviewService.addOrModifyVendorStarReview(memberId, vendorId, ratingEnv, ratingPrice, ratingService);
//
//		return "/vendor/vendor_detail.html";
//	}
	@ResponseBody
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

//	@PostMapping("/vendor/give_vendor_like")
//	public String giveVendorLike(Integer memberId, Integer vendorId) {
//		vendorLikeService.addOrCancelVendorLike(memberId, vendorId);
//		
//		return "/vendor/vendor_detail.html";
//	}

	@ResponseBody
	@PostMapping("/vendor/give_vendor_like")
	public Map<String, Object> giveVendorLike(@RequestBody Map<String, Integer> data) {
		Integer memberId = data.get("memberId");
		Integer vendorId = data.get("vendorId");

		boolean isLiked = vendorLikeService.addOrCancelVendorLike(memberId, vendorId);

		Map<String, Object> response = new HashMap<>();
		response.put("action", isLiked ? true : false);
		return response;
	}

//	@DeleteMapping("/vendor/delete_review")
//	public String deleteReview(Integer memberId, Integer vendorId) {
//		vendorReviewService.deleteReviewByMemberIdAndVendorId(memberId, vendorId);
//		return "/vendor/vendor_detail.html";
//	}
	
	@ResponseBody
	@DeleteMapping("/vendor/delete_review")
	public Map<String, Object> deleteReview(@RequestBody Map<String, Integer> data) {
		Integer memberId = data.get("memberId");
		Integer vendorId = data.get("vendorId");
		
		vendorReviewService.deleteReviewByMemberIdAndVendorId(memberId, vendorId);
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		return response;
	}

	@ResponseBody
	@GetMapping("/vendor/find_by_category")
	public List<Vendor> findVendorByCategory(Integer categoryId) {
		List<Vendor> vendorList = vendorService.findVendorByCategoryId(categoryId);
		return vendorList;
	}

	@ResponseBody
	@GetMapping("/vendor/find_by_keyword")
	public List<Vendor> findVendorByKeyword(String keyword) {
		List<Vendor> vendorList = vendorService.findVendorByNameOrDescription(keyword);
		return vendorList;
	}

	@PostMapping("/vendor/add_review_photos")
	public String addReviewPhotos(Integer reviewId, List<MultipartFile> reviewPhotos) throws IOException {
		vendorReviewService.addReviewPhotos(reviewId, reviewPhotos);

		return "/vendor/vendor_detail.html";
	}

}
