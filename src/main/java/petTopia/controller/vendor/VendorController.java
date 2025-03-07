package petTopia.controller.vendor;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorImages;
import petTopia.service.vendor.VendorImagesService;
import petTopia.service.vendor.VendorLikeService;
import petTopia.service.vendor.VendorReviewService;
import petTopia.service.vendor.VendorService;

@Controller
public class VendorController {

	@Autowired
	private VendorService vendorService;

	@Autowired
	private VendorReviewService vendorReviewService;
	
	@Autowired
	private VendorLikeService vendorLikeService;
	
	@Autowired
	private VendorImagesService vendorImagesService;

	@GetMapping("/vendor")
	public String vendorHome(Model model) {

		List<Vendor> vendorList = vendorService.findAllVendor();
		model.addAttribute("vendorList", vendorList);

		return "/vendor/vendor_home.html";
	}

	@GetMapping("/vendor/detail/{vendorId}")
	public String vendorDetail(@PathVariable Integer vendorId, Model model) {

		/* 該店家資料之賦值 */
		Vendor vendor = vendorService.findVendorById(vendorId);
		model.addAttribute("vendor", vendor);

		/* 所有店家資料之賦值 */
		List<Vendor> vendorList = vendorService.findAllVendor();
		model.addAttribute("vendorList", vendorList);

		/* 該店家評論之賦值 */
		List<VendorReviewDto> reviewList = vendorReviewService.getReviewListByVendorId(vendorId);
		model.addAttribute("reviewList", reviewList);
		
		/* 該店家所有圖片賦值 */
		List<VendorImages> imageList = vendorImagesService.findImagesByVendorId(vendorId);
		model.addAttribute("imageList",imageList);

		return "/vendor/vendor_detail.html";
	}

	@PostMapping("/vendor/give_text_review")
	public String giveTextReview(Integer memberId, Integer vendorId, String context) {
		vendorReviewService.addOrModifyVendorTextReview(memberId, vendorId, context);

		return "/vendor/vendor_detail.html";
	}

	@PostMapping("/vendor/give_star_review")
	public String giveStarReview(Integer memberId, Integer vendorId, Integer ratingEnv, Integer ratingPrice,
			Integer ratingService) {
		vendorReviewService.addOrModifyVendorStarReview(memberId, vendorId, ratingEnv, ratingPrice, ratingService);

		return "/vendor/vendor_detail.html";
	}
	
	@PostMapping("/vendor/give_vendor_like")
	public String giveVendorLike(Integer memberId, Integer vendorId) {
		vendorLikeService.addOrCancelVendorLike(memberId, vendorId);
		
		return "/vendor/vendor_detail.html";
	}
	
	@DeleteMapping("/vendor/delete_review")
	public String deleteReview(Integer memberId, Integer vendorId) {
		vendorReviewService.deleteReviewByMemberIdAndVendorId(memberId, vendorId);
		return "/vendor/vendor_detail.html"; 
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
