package petTopia.controller.vendor;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.vendor.Vendor;
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
		String logoImgString = Base64.getEncoder().encodeToString(vendor.getLogoImg());
		vendor.setLogoImgBase64(logoImgString);
		model.addAttribute("vendor", vendor);

		/* 所有店家資料之賦值 */
		List<Vendor> vendorList = vendorService.findAllVendor();
		model.addAttribute("vendorList", vendorList);

		/* 該店家評論之賦值 */
		List<VendorReviewDto> reviewList = vendorReviewService.getReviewListByVendorId(vendorId);
		model.addAttribute("reviewList", reviewList);

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
	
	@PostMapping("/vendor/delete_review")
	public String deleteReview(Integer memberId, Integer vendorId) {
		vendorReviewService.deleteReviewByMemberIdAndVendorId(memberId, vendorId);
		return "/vendor/vendor_detail.html"; 
	}
}
