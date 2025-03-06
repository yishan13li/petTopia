package petTopia.controller.vendor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import petTopia.model.vendor.VendorActivity;
import petTopia.service.vendor.VendorActivityReviewService;
import petTopia.service.vendor.VendorActivityService;

@Controller
public class ActivityController {
	
	@Autowired
	private VendorActivityService vendorActivityService;
	
	@Autowired
	private VendorActivityReviewService vendorActivityReviewService;
	
	@GetMapping("/activity")
	public String activityHome() {
		
		return "/vendor/activity_home.html";
	}
	
	@GetMapping("/activity/detail/{activityId}")
	public String activityDetail(@PathVariable Integer activityId, Model model) {
		
		/* 該活動資料之賦值 */
		VendorActivity activity = vendorActivityService.findActivityById(activityId);
		model.addAttribute("activity", activity);
		
		return "/vendor/activity_detail.html";
	}
	
	@PostMapping("/activity/give_review")
	public String giveReview(Integer memberId, Integer activityId, String context) {
		vendorActivityReviewService.addOrModifyActivityReview(memberId, activityId, context);

		return "/vendor/vendor_detail.html"; 
	}
	
	@DeleteMapping("/activity/delete_review")
	public String deleteReview(Integer memberId, Integer activityId) {
		vendorActivityReviewService.deleteReviewByMemberIdAndVendorId(memberId, activityId);
		return "/vendor/vendor_detail.html"; 
	}
}
