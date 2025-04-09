package petTopia.controller.vendor.thymeleaf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorImages;
import petTopia.service.vendor.VendorImagesService;
import petTopia.service.vendor.VendorLikeService;
import petTopia.service.vendor.VendorReviewService;
import petTopia.service.vendor.VendorService;

@Controller
public class ThymeleafVendorController {

	@Autowired
	private VendorService vendorService;

	@Autowired
	private VendorReviewService vendorReviewService;

	@Autowired
	private VendorImagesService vendorImagesService;
	
	@Autowired
	private VendorLikeService vendorLikeService;

	@GetMapping("/vendor")
	public String vendorHome(Model model) {

		List<Vendor> vendorList = vendorService.findAllVendor();
		model.addAttribute("vendorList", vendorList);

		return "/vendor/vendor_home.html";
	}

	@GetMapping("/vendor/detail/{vendorId}")
	public String vendorDetail(@PathVariable("vendorId") Integer vendorId, Model model) {

		/* 該店家資料之賦值 */
		Vendor vendor = vendorService.findVendorById(vendorId);
		model.addAttribute("vendor", vendor);

		/* 所有店家資料之賦值 */
		List<Vendor> vendorList = vendorService.findAllVendor();
		model.addAttribute("vendorList", vendorList);

		/* 該店家評論之賦值 */
		List<VendorReviewDto> reviewList = vendorReviewService.findReviewListByVendorId(vendorId);
		model.addAttribute("reviewList", reviewList); // (reviewList != null) ? reviewList : new ArrayList<>())

		/* 該店家所有圖片賦值 */
		List<VendorImages> imageList = vendorImagesService.findImageListByVendorId(vendorId);
		model.addAttribute("imageList", imageList); // (imageList != null) ? imageList : new ArrayList<>()

		return "/vendor/vendor_detail.html";
	}
	
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
		vendorReviewService.addStarReview(memberId, vendorId, ratingEnv, ratingPrice, ratingService);

		Map<String, Object> response = new HashMap<>();
		response.put("sucess", true);
		return response;
	}

	@PostMapping("/vendor/give_vendor_like")
	public Map<String, Object> giveVendorLike(@RequestBody Map<String, Integer> data) {
		Integer memberId = data.get("memberId");
		Integer vendorId = data.get("vendorId");

		boolean isLiked = vendorLikeService.toggleVendorLike(memberId, vendorId);

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
	
	@PostMapping("/api/vendor/review/{reviewId}/modify")
	public Map<String, Object> modifyReview(@PathVariable Integer reviewId, @RequestParam String content) {
		vendorReviewService.rewriteReviewById(reviewId, content);

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		return response;
	}

}
